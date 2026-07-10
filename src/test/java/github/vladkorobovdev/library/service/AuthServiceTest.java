package github.vladkorobovdev.library.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import github.vladkorobovdev.library.config.JwtUtil;
import github.vladkorobovdev.library.model.dto.SignupRequest;
import github.vladkorobovdev.library.model.entity.User;
import github.vladkorobovdev.library.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private UserRepository userRepo;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private JwtUtil jwtUtil;

  @InjectMocks
  private AuthService authService;

  @Test
  void register_ShouldReturnTrue_WhenDataIsValid() {
    SignupRequest request = new SignupRequest("user1", "pass", "John", "Doe");
    when(userRepo.findByLogin(request.login())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(request.password())).thenReturn("hashedPass");

    boolean result = authService.register(request);

    assertTrue(result);
    verify(userRepo).save(any(User.class));
  }

  @Test
  void register_ShouldReturnFalse_WhenLoginAlreadyExists() {
    SignupRequest request = new SignupRequest("user1", "pass", "John", "Doe");
    when(userRepo.findByLogin(request.login())).thenReturn(Optional.of(new User()));

    boolean result = authService.register(request);

    assertFalse(result);
    verify(userRepo, never()).save(any(User.class));
  }

  @Test
  void login_ShouldReturnJwtToken_WhenCredentialsAreValid() {
    String login = "user1";
    String password = "password";
    String expectedToken = "jwt.token.here";

    Authentication authentication = mock(Authentication.class);
    UserDetails userDetails = mock(UserDetails.class);

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(jwtUtil.generateToken(userDetails)).thenReturn(expectedToken);

    String token = authService.login(login, password);

    assertEquals(expectedToken, token);
  }
}
