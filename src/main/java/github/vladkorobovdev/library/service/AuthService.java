package github.vladkorobovdev.library.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import github.vladkorobovdev.library.config.JwtUtil;
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

  public boolean register(User user) {
    if (user.getLogin() == null ||
        user.getLogin().isBlank() ||
        user.getPassword() == null ||
        user.getPassword().isBlank() ||
        user.getFirstname() == null ||
        user.getFirstname().isBlank() ||
        user.getLastname() == null ||
        user.getLastname().isBlank())
      return false;
    if (userRepo.findByLogin(user.getLogin()).isPresent())
      return false;

    User newUser = User.builder()
        .login(user.getLogin())
        .firstname(user.getFirstname())
        .lastname(user.getLastname())
        .password(passwordEncoder.encode(user.getPassword()))
        .role(Role.USER)
        .build();

    userRepo.save(newUser);
    return true;
  }
}
