package gr.aueb.cf3.tradingjournalapp.controller;

import gr.aueb.cf3.tradingjournalapp.dto.AuthDTO;
import gr.aueb.cf3.tradingjournalapp.dto.LoginDTO;
import gr.aueb.cf3.tradingjournalapp.dto.UserDTO;
import gr.aueb.cf3.tradingjournalapp.service.LoginService;
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
    private final LoginService loginService;

    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully registered and signed-in",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Teachers Found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthDTO.class))})
    })
    @PostMapping({"/register"})
    public ResponseEntity<AuthDTO> register(@RequestBody @Valid UserDTO userDTO) {
        return ResponseEntity.ok(loginService.register(userDTO));
    }

    @PostMapping({"/login"})
    public ResponseEntity<AuthDTO> login(@RequestBody @Valid LoginDTO loginDTO) {
        return ResponseEntity.ok(loginService.login(loginDTO));
    }

    @PostMapping({"/refresh-token"})
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        loginService.refreshToken(request, response);
    }
}
