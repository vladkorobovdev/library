package github.vladkorobovdev.library.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.vladkorobovdev.library.model.entity.Role;
import github.vladkorobovdev.library.model.entity.User;
import github.vladkorobovdev.library.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }

  @Transactional
  public User create(User user) {
    user.setRole(Role.USER);

    return userRepository.save(user);
  }

  @Transactional
  public Optional<User> update(Long id, User newUser) {
    return userRepository.findById(id)
        .map(user -> {
          user.setUsername(newUser.getUsername());
          user.setPassword(newUser.getPassword());
          return user;
        });
  }

  @Transactional
  public void deleteById(Long id) {
    userRepository.deleteById(id);
  }
}
