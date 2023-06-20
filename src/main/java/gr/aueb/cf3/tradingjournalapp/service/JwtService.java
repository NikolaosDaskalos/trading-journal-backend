package gr.aueb.cf3.tradingjournalapp.service;

import gr.aueb.cf3.tradingjournalapp.model.Token;
import gr.aueb.cf3.tradingjournalapp.model.TokenType;
import gr.aueb.cf3.tradingjournalapp.model.User;
import gr.aueb.cf3.tradingjournalapp.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private final TokenRepository tokenRepository;
    private static final String ACCESS_TOKEN = "Access Token";
    private static final String REFRESH_TOKEN = "Refresh Token";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claim -> (Long) claim.get("id"));
    }

    public String generateToken(User user) {
        return buildToken(Map.of("id", user.getId(), "type", ACCESS_TOKEN), user, jwtExpiration);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }

    public boolean isTokenTypeValid(String token, String tokenType) {
        return tokenType.equals(extractTokenType(token));
    }

    private String extractTokenType(String token) {
        return extractClaim(token, c -> (String) c.get("type"));
    }

    public String generateRefreshToken(User user) {
        return buildToken(Map.of("id", user.getId(), "type", REFRESH_TOKEN), user, refreshTokenExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, User user, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("jwt has expired", e);
            revokeToken(token);
            throw new JwtException("jwt has expired");
        } catch (Exception e) {
            log.error("other error with jwt", e);
            throw new JwtException("error with jwt");
        }
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void revokeAllUserTokens(User user, TokenType tokenType) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId(), tokenType);

        if (!validUserTokens.isEmpty()) {
            validUserTokens.forEach(t -> {
                t.setExpired(true);
                t.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }
    }

    private void revokeToken(String token) {
        Token retrievedToken = tokenRepository.findByToken(token).orElse(null);

        if (retrievedToken != null) {
            retrievedToken.setRevoked(true);
            retrievedToken.setExpired(true);
            tokenRepository.save(retrievedToken);
            log.info("token of user {} with tokenId {} of type {} has been revoked", retrievedToken.getUser().getUsername(), retrievedToken.getId(), retrievedToken.getTokenType());
        }

    }

}
