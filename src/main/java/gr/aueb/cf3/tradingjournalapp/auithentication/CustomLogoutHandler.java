package gr.aueb.cf3.tradingjournalapp.auithentication;

import gr.aueb.cf3.tradingjournalapp.model.Token;
import gr.aueb.cf3.tradingjournalapp.model.TokenType;
import gr.aueb.cf3.tradingjournalapp.model.User;
import gr.aueb.cf3.tradingjournalapp.repository.TokenRepository;
import gr.aueb.cf3.tradingjournalapp.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private static final String BEARER = "Bearer ";

    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith(BEARER)) {
            String jwt = authHeader.substring("Bearer ".length());
            Token storedToken = tokenRepository.findByToken(jwt).orElse(null);
            if (storedToken != null && !isTokenExpiredOrRevoked(storedToken)) {
                User user = storedToken.getUser();
                jwtService.revokeAllUserTokens(user, TokenType.BEARER_ACCESS);
                jwtService.revokeAllUserTokens(user, TokenType.BEARER_REFRESH);
                log.info("User {} logged out", storedToken.getUser().getUsername());
            } else {
                response.setStatus(404);
            }
        } else {
            response.setStatus(404);
        }
    }

    private boolean isTokenExpiredOrRevoked(Token token) {
        return token.isExpired() || token.isRevoked();
    }

}

