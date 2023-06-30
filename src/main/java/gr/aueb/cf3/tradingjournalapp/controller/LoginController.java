package gr.aueb.cf3.tradingjournalapp.controller;

import gr.aueb.cf3.tradingjournalapp.dto.AuthDTO;
import gr.aueb.cf3.tradingjournalapp.dto.LoginDTO;
import gr.aueb.cf3.tradingjournalapp.dto.UserDTO;
import gr.aueb.cf3.tradingjournalapp.service.ILoginService;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.EmailAlreadyExistsException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UsernameAlreadyExistsException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {
    private final ILoginService loginService;

    @Operation(summary = "Register User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registered successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthDTO.class))}),
            @ApiResponse(responseCode = "409", description = "Username or email already exists",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Missing user fields",
                    content = @Content)})
    @PostMapping({"/register"})
    public ResponseEntity<AuthDTO> register(@RequestBody @Valid UserDTO userDTO) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        return ResponseEntity.ok(loginService.register(userDTO));
    }

    @Operation(summary = "User Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged in successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Wrong credentials",
                    content = @Content)})
    @PostMapping({"/login"})
    public ResponseEntity<AuthDTO> login(@RequestBody @Valid LoginDTO loginDTO) {
        return ResponseEntity.ok(loginService.login(loginDTO));
    }

    @Operation(summary = "Refresh access Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Invalid refresh token",
                    content = @Content)})
    @PostMapping({"/refresh-token"})
    public ResponseEntity<AuthDTO> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return ResponseEntity.ok(loginService.refreshToken(request, response));
    }
}
