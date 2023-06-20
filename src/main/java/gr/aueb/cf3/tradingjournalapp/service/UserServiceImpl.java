package gr.aueb.cf3.tradingjournalapp.service;

import gr.aueb.cf3.tradingjournalapp.dto.UserDTO;
import gr.aueb.cf3.tradingjournalapp.model.TokenType;
import gr.aueb.cf3.tradingjournalapp.model.User;
import gr.aueb.cf3.tradingjournalapp.repository.UserRepository;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.EmailAlreadyExistsException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UserNotFoundException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UsernameAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
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
        } else {
            return user.get();
        }
    }

    public User findUserByUsername(String username) throws UserNotFoundException {
        log.info("Finding user with username {}", username);

        User user = userRepository.findUserByUsername(username);

        if (Objects.isNull(user)) {
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
    public User createUser(UserDTO userDTO) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        log.info("Creating user with username {}", userDTO.getUsername());
        User user = mapToUser(userDTO);
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (userRepository.isUsernameExists(user.getUsername())) {
            log.warn("This Username: {} already exists  user is not created", user.getUsername());
            throw new UsernameAlreadyExistsException(user.getUsername());
        } else if (userRepository.isEmailExists(user.getEmail())) {
            log.warn("This Email: {} already exists user is not created", user.getEmail());
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(UserDTO userDTO) throws UserNotFoundException {
        log.info("Updating user with username {}", userDTO.getUsername());
        User user = mapToUser(userDTO);

        Optional<User> oldUser = userRepository.findByUsername(user.getUsername());
        if (oldUser.isEmpty()) {
            log.error("User {} do not exist", user.getId());
            throw new UserNotFoundException(user.getUsername());
        }

        if (!passwordEncoder.matches(user.getPassword(), (oldUser.get()).getPassword())) {
            log.info("Updating password of user: {}", user.getId());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }


    @Transactional
    public void deleteUser(String username) throws UserNotFoundException {
        User user = userRepository.findUserByUsername(username);

        if (user == null) {
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
                .password(userDTO.getPassword())
                .email(userDTO.getEmail().trim())
                .build();
    }
}
