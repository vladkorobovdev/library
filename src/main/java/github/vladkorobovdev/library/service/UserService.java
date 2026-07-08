package github.vladkorobovdev.library.service;

import java.util.List;
import java.util.Optional;

import github.vladkorobovdev.library.model.dto.SignupRequest;
import github.vladkorobovdev.library.model.entity.User;

public interface UserService {
  List<User> findAll();

  Optional<User> findById(Long id);

  Optional<User> findByLogin(String login);

  // User create(UserDTO user);

  Optional<User> update(Long id, SignupRequest user);

  void deleteById(Long id);

}
