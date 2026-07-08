package github.vladkorobovdev.library.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import github.vladkorobovdev.library.config.JwtUtil;
import github.vladkorobovdev.library.model.dto.SignupRequest;
import github.vladkorobovdev.library.model.entity.Role;
import github.vladkorobovdev.library.model.entity.User;
import github.vladkorobovdev.library.repository.UserRepository;

@Service
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepo;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public AuthService(AuthenticationManager authenticationManager, UserRepository userRepo,
      PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  public String login(String login, String password) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(login, password));

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    return jwtUtil.generateToken(userDetails);
  }

  public boolean register(SignupRequest request) {
    if (request.login() == null ||
        request.login().isBlank() ||
        request.password() == null ||
        request.password().isBlank() ||
        request.firstName() == null ||
        request.firstName().isBlank() ||
        request.lastName() == null ||
        request.lastName().isBlank())
      return false;
    if (userRepo.findByLogin(request.login()).isPresent())
      return false;

    User newUser = User.builder()
        .login(request.login())
        .firstname(request.firstName())
        .lastname(request.lastName())
        .password(passwordEncoder.encode(request.password()))
        .role(Role.USER)
        .build();

    userRepo.save(newUser);
    return true;
  }
}
