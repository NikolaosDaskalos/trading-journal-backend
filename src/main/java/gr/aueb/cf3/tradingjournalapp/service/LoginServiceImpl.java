package gr.aueb.cf3.tradingjournalapp.service;

import gr.aueb.cf3.tradingjournalapp.dto.AuthDTO;
import gr.aueb.cf3.tradingjournalapp.dto.LoginDTO;
import gr.aueb.cf3.tradingjournalapp.dto.UserDTO;
import gr.aueb.cf3.tradingjournalapp.model.Role;
import gr.aueb.cf3.tradingjournalapp.model.Token;
import gr.aueb.cf3.tradingjournalapp.model.TokenType;
import gr.aueb.cf3.tradingjournalapp.model.User;
import gr.aueb.cf3.tradingjournalapp.repository.TokenRepository;
import gr.aueb.cf3.tradingjournalapp.repository.UserRepository;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.EmailAlreadyExistsException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UsernameAlreadyExistsException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements ILoginService {
    private static final String BEARER = "Bearer ";
    private static final String REFRESH_TOKEN = "Refresh Token";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    @Transactional
    public AuthDTO register(UserDTO userDTO) throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        if (userRepository.isEmailExists(userDTO.getEmail().trim())) {
            log.warn("Email {} already exists", userDTO.getEmail());
            throw new EmailAlreadyExistsException(userDTO.getEmail());
        }

        if (userRepository.isUsernameExists(userDTO.getUsername().trim())) {
            log.warn("Username {} already exists", userDTO.getUsername());
            throw new UsernameAlreadyExistsException(userDTO.getUsername());
        }

        User savedUser = userRepository.save(mapToUser(userDTO));

        String jwtToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        saveToken(savedUser, jwtToken, TokenType.BEARER_ACCESS);
        saveToken(savedUser, refreshToken, TokenType.BEARER_REFRESH);

        return new AuthDTO(jwtToken, refreshToken);
    }

    @Transactional
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

    @Transactional
    public AuthDTO refreshToken(HttpServletRequest request, HttpServletResponse response)  {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        String refreshToken = authHeader.substring(BEARER.length());
        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByUsername(username).orElseThrow();

        if (jwtService.isTokenValid(refreshToken, user) && jwtService.isTokenTypeValid(refreshToken, REFRESH_TOKEN)) {
            String accessToken = jwtService.generateToken(user);
            jwtService.revokeAllUserTokens(user, TokenType.BEARER_ACCESS);
            saveToken(user, accessToken, TokenType.BEARER_ACCESS);
            return new AuthDTO(accessToken, refreshToken);
        }
        throw new JwtException("this refresh token is invalid");
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
