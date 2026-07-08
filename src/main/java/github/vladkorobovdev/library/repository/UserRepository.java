package github.vladkorobovdev.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import github.vladkorobovdev.library.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  public Optional<User> findByLogin(String login);
}
