package gr.aueb.cf3.tradingjournalapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aueb.cf3.tradingjournalapp.dto.AuthDTO;
import gr.aueb.cf3.tradingjournalapp.dto.LoginDTO;
import gr.aueb.cf3.tradingjournalapp.dto.UserDTO;
import gr.aueb.cf3.tradingjournalapp.model.Role;
import gr.aueb.cf3.tradingjournalapp.model.Token;
import gr.aueb.cf3.tradingjournalapp.model.TokenType;
import gr.aueb.cf3.tradingjournalapp.model.User;
import gr.aueb.cf3.tradingjournalapp.repository.TokenRepository;
import gr.aueb.cf3.tradingjournalapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private static final String BEARER = "Bearer ";
    private static final String REFRESH_TOKEN = "Refresh Token";

    public AuthDTO register(UserDTO userDTO) {
        User savedUser = userRepository.save(mapToUser(userDTO));

        String jwtToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        saveToken(savedUser, jwtToken, TokenType.BEARER_ACCESS);
        saveToken(savedUser, refreshToken, TokenType.BEARER_REFRESH);

        return new AuthDTO(jwtToken, refreshToken);
    }

    public AuthDTO login(LoginDTO loginDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
        User user = userRepository.findByUsername(loginDTO.getUsername()).orElseThrow();

        String jwtToken = jwtService.generateToken(user);
        jwtService.revokeAllUserTokens(user, TokenType.BEARER_ACCESS);
        saveToken(user, jwtToken, TokenType.BEARER_ACCESS);

        String refreshToken = jwtService.generateRefreshToken(user);
        jwtService.revokeAllUserTokens(user, TokenType.BEARER_REFRESH);
        saveToken(user, refreshToken, TokenType.BEARER_REFRESH);

        return new AuthDTO(jwtToken, refreshToken);
    }

    private void saveToken(User user, String jwtToken, TokenType tokenType) {
        tokenRepository.save(Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(tokenType)
                .revoked(false)
                .expired(false)
                .build());
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        String refreshToken = authHeader.substring(BEARER.length());
        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByUsername(username).orElseThrow();

        if (jwtService.isTokenValid(refreshToken, user) && jwtService.isTokenTypeValid(refreshToken, REFRESH_TOKEN)) {
            String accessToken = jwtService.generateToken(user);
            jwtService.revokeAllUserTokens(user, TokenType.BEARER_ACCESS);
            saveToken(user, accessToken, TokenType.BEARER_ACCESS);
            AuthDTO authDTO = new AuthDTO(accessToken, refreshToken);
            new ObjectMapper().writeValue(response.getOutputStream(), authDTO);
        }
    }

    private User mapToUser(UserDTO userDTO) {
        return User.builder()
                .firstname(userDTO.getName())
                .lastname(userDTO.getLastname())
                .age(userDTO.getAge())
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getEmail())
                .role(Role.USER).build();
    }
}
