
package gr.aueb.cf3.tradingjournalapp.controller;

import gr.aueb.cf3.tradingjournalapp.dto.UserDTO;
import gr.aueb.cf3.tradingjournalapp.model.User;
import gr.aueb.cf3.tradingjournalapp.service.IUserService;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.EmailAlreadyExistsException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UserNotFoundException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UsernameAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.hibernate5.HibernateQueryException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UsersController {
    private final IUserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            List<User> users = userService.findAllUsers();
            List<UserDTO> usersDTO = users.stream().map(this::mapToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(usersDTO);
        } catch (HibernateQueryException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("userId") Long userId) {
        if (Objects.isNull(userId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            try {
                User user = userService.findUserById(userId);
                return ResponseEntity.ok(mapToDTO(user));
            } catch (UserNotFoundException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } catch (HibernateQueryException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping(path = "/users", params = "username")
    public ResponseEntity<UserDTO> getUserByUsername(@RequestParam("username") String username) {
        if (StringUtils.isBlank(username)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            try {
                User user = userService.findUserByUsername(username.trim());
                return ResponseEntity.ok(mapToDTO(user));
            } catch (UserNotFoundException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } catch (HibernateQueryException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> addUser(@RequestBody @Valid UserDTO dto) {
        try {
            User user = userService.createUser(dto);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
            return ResponseEntity.created(location).body(mapToDTO(user));
        } catch (EmailAlreadyExistsException | UsernameAlreadyExistsException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("userId") @Valid Long userId, @RequestBody UserDTO dto) {
        try {
            dto.setId(userId);
            User updatedUser = userService.updateUser(dto);
            return ResponseEntity.ok(mapToDTO(updatedUser));
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HibernateQueryException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable("userId") Long userId) {
        try {
            User deletedUser = userService.deleteUser(userId);
            return ResponseEntity.ok(mapToDTO(deletedUser));
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (HibernateQueryException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getFirstname())
                .lastname(user.getLastname())
                .age(user.getAge())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
