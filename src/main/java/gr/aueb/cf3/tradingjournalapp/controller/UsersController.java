
package gr.aueb.cf3.tradingjournalapp.controller;

import gr.aueb.cf3.tradingjournalapp.dto.UserDTO;
import gr.aueb.cf3.tradingjournalapp.model.User;
import gr.aueb.cf3.tradingjournalapp.service.IUserService;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.EmailAlreadyExistsException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UsersController {
    private final IUserService userService;

    @Operation(summary = "Find user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "user found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "404", description = "User Not found",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized to get information for another user",
                    content = @Content)})
    @GetMapping(path = "/users/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable("username") String username, Principal principal) throws UserNotFoundException {
        if (StringUtils.isBlank(username)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!username.equals(principal.getName())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Email already exists",
                    content = @Content)})
    @PutMapping("/users")
        public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO dto, Principal principal) throws EmailAlreadyExistsException, UserNotFoundException {
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
    public ResponseEntity<?> deleteUser(Principal principal) throws UserNotFoundException {
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
