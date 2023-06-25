
package gr.aueb.cf3.tradingjournalapp.controller;

import gr.aueb.cf3.tradingjournalapp.dto.UserDTO;
import gr.aueb.cf3.tradingjournalapp.model.User;
import gr.aueb.cf3.tradingjournalapp.service.IUserService;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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

    @Operation(summary = "Find all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))})})
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        List<UserDTO> usersDTO = users.stream().map(this::mapToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(usersDTO);
    }

    @Operation(summary = "Find user by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "user found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "404", description = "User Not found",
                    content = @Content)})
    @GetMapping("/users/{userId}")
    @SneakyThrows
    public ResponseEntity<UserDTO> getUserById(@PathVariable("userId") Long userId) {
        if (Objects.isNull(userId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = userService.findUserById(userId);
        return ResponseEntity.ok(mapToDTO(user));
    }

    @Operation(summary = "Find user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "user found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "404", description = "User Not found",
                    content = @Content)})
    @GetMapping(path = "/users", params = "username")
    @SneakyThrows
    public ResponseEntity<UserDTO> getUserByUsername(@RequestParam("username") String username) {
        if (StringUtils.isBlank(username)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = userService.findUserByUsername(username.trim());
        return ResponseEntity.ok(mapToDTO(user));
    }


    @Operation(summary = "Update user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "user updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "404", description = "User Not found",
                    content = @Content)})
    @PutMapping("/users")
    @SneakyThrows
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO dto, Principal principal) {
        if (!principal.getName().equals(dto.getUsername().trim())) {
            throw new UserNotFoundException(dto.getUsername());
        }

        User updatedUser = userService.updateUser(dto);
        return ResponseEntity.ok(mapToDTO(updatedUser));
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "user deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "404", description = "User Not found",
                    content = @Content)})
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
