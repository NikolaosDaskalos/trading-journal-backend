
package gr.aueb.cf3.tradingjournalapp.controller;

import gr.aueb.cf3.tradingjournalapp.dto.UserDTO;
import gr.aueb.cf3.tradingjournalapp.model.User;
import gr.aueb.cf3.tradingjournalapp.service.IUserService;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.security.Principal;
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
        List<User> users = userService.findAllUsers();
        List<UserDTO> usersDTO = users.stream().map(this::mapToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(usersDTO);
    }

    @GetMapping("/users/{userId}")
    @SneakyThrows
    public ResponseEntity<UserDTO> getUserById(@PathVariable("userId") Long userId) {
        if (Objects.isNull(userId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = userService.findUserById(userId);
        return ResponseEntity.ok(mapToDTO(user));
    }

    @GetMapping(path = "/users", params = "username")
    @SneakyThrows
    public ResponseEntity<UserDTO> getUserByUsername(@RequestParam("username") String username) {
        if (StringUtils.isBlank(username)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = userService.findUserByUsername(username.trim());
        return ResponseEntity.ok(mapToDTO(user));
    }

    @PostMapping("/users")
    @SneakyThrows
    public ResponseEntity<UserDTO> addUser(@RequestBody @Valid UserDTO dto) {
        User user = userService.createUser(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(location).body(mapToDTO(user));
    }


    @PutMapping("/users")
    @SneakyThrows
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO dto, Principal principal) {
        if (!principal.getName().equals(dto.getUsername().trim())) {
            throw new UserNotFoundException(dto.getUsername());
        }

        User updatedUser = userService.updateUser(dto);
        return ResponseEntity.ok(mapToDTO(updatedUser));
    }

    @DeleteMapping("/users")
    @SneakyThrows
    public ResponseEntity<?> deleteUser(Principal principal) {
        userService.deleteUser(principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);

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
