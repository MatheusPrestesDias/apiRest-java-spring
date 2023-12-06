package br.com.dias.apiRest.controller;

import br.com.dias.apiRest.data.dto.v1.security.AccountCredentialsDTO;
import br.com.dias.apiRest.data.dto.v1.security.TokenDTO;
import br.com.dias.apiRest.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@Tag(name="Authentication Endpoint")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Authenticates a user and returns a token")
    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AccountCredentialsDTO data) {
        try {
            TokenDTO tokenResponse = authService.authenticate(data);
            return ResponseEntity.ok(tokenResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    @Operation(summary = "Refresh token for authenticates a user and returns a token")
    @PutMapping(value = "/refresh/{username}")
    public ResponseEntity<?> refreshToken(
            @NotBlank(message = "Username must not be blank")
            @PathVariable("username") String username,
            @NotBlank(message = "Refresh token must not be blank")
            @RequestHeader("Authorization") String refreshToken
    ) {
        try {
            TokenDTO tokenResponse = authService.refreshToken(username, refreshToken);
            return ResponseEntity.ok(tokenResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

}
