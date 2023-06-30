package gr.aueb.cf3.tradingjournalapp.service;

import gr.aueb.cf3.tradingjournalapp.dto.UserDTO;
import gr.aueb.cf3.tradingjournalapp.model.TokenType;
import gr.aueb.cf3.tradingjournalapp.model.User;
import gr.aueb.cf3.tradingjournalapp.repository.UserRepository;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.EmailAlreadyExistsException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User findUserById(Long id) throws UserNotFoundException {
        log.info("Finding user with id {}", id);

        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            log.warn("User with id {} not found", id);
            throw new UserNotFoundException(id);
        }

        return user.get();
    }

    public User findUserByUsername(String username) throws UserNotFoundException {
        log.info("Finding user with username {}", username);

        User user = userRepository.findUserByUsername(username);

        if (user == null) {
            log.warn("User with username {} not found", username);
            throw new UserNotFoundException(username);
        }
        return user;

    }

    public List<User> findAllUsers() {
        log.info("Finding all Users");
        return userRepository.findAll();

    }

    @Transactional
    public User updateUser(UserDTO userDTO) throws UserNotFoundException, EmailAlreadyExistsException {
        log.info("Updating user with username {}", userDTO.getUsername());
        User updateUser = mapToUser(userDTO);

        User oldUser = userRepository.findByUsername(updateUser.getUsername())
                .orElseThrow(() -> {
                    log.error("Update canceled User {} do not exist", updateUser.getUsername());
                    return new UserNotFoundException(updateUser.getUsername());
                });

        if (!oldUser.getEmail().equals(updateUser.getEmail()) && userRepository.isEmailExists(updateUser.getEmail())) {
            log.error("Update canceled User with email {} already exist", updateUser.getEmail());
            throw new EmailAlreadyExistsException(updateUser.getEmail());
        }
        updateUser.setId(oldUser.getId());
        updateUser.setRole(oldUser.getRole());
        updateUser.setTokens(oldUser.getTokens());
        updateUser.setTrades(oldUser.getTrades());
        updateUser.setStatistics(oldUser.getStatistics());
        return userRepository.save(updateUser);
    }


    @Transactional
    public void deleteUser(String username) throws UserNotFoundException {
        User user = userRepository.findUserByUsername(username);

        if (user == null) {
            log.error("Delete user failed user with username {} not found", username);
            throw new UserNotFoundException(username);
        }

        userRepository.deleteById(user.getId());
        jwtService.revokeAllUserTokens(user, TokenType.BEARER_ACCESS);
        jwtService.revokeAllUserTokens(user, TokenType.BEARER_REFRESH);
    }

    private User mapToUser(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .firstname(userDTO.getName().trim())
                .lastname(userDTO.getLastname().trim())
                .age(userDTO.getAge())
                .username(userDTO.getUsername().trim())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getEmail().trim())
                .build();
    }
}
