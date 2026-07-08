package github.vladkorobovdev.library.service;

import java.util.List;
import java.util.Optional;

import github.vladkorobovdev.library.model.entity.User;

public interface UserService {
  List<User> findAll();

  Optional<User> findById(Long id);

  User create(User book);

  Optional<User> update(Long id, User book);

  void deleteById(Long id);

}
