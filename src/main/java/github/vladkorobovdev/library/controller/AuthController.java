package github.vladkorobovdev.library.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.vladkorobovdev.library.model.dto.LoginRequest;
import github.vladkorobovdev.library.model.dto.LoginResponse;
import github.vladkorobovdev.library.model.dto.SignupRequest;
import github.vladkorobovdev.library.service.AuthService;

@RestController
@RequestMapping("api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
      @RequestBody LoginRequest loginRequest) {
    try {
      String token = authService.login(loginRequest.login(), loginRequest.password());
      return ResponseEntity.ok(new LoginResponse(token));
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @PostMapping("/register")
  public ResponseEntity<String> register(
      @RequestBody SignupRequest request) {

    boolean isRegistered = authService.register(request);

    if (isRegistered) {
      return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Login already taken or invalid data.");
    }
  }
}
