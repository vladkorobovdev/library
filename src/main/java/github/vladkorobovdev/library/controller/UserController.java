package github.vladkorobovdev.library.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.vladkorobovdev.library.model.dto.UserDTO;
import github.vladkorobovdev.library.model.entity.User;
import github.vladkorobovdev.library.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping()
  public List<UserDTO> getAllUsers() {
    List<User> users = userService.findAll();
    List<UserDTO> dtos = new ArrayList<>();
    for (User user : users) {
      var userDto = new UserDTO(user.getId(), user.getLogin(), user.getFirstname(), user.getLastname(), user.getRole());
      dtos.add(userDto);
    }
    return dtos;
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
    return ResponseEntity.of(userService.findById(id)
        .map(user -> new UserDTO(user.getId(), user.getLogin(), user.getFirstname(), user.getLastname(),
            user.getRole())));
  }

  // @PostMapping
  // public ResponseEntity<User> createUser(@RequestBody User user) {
  // User savedUser = userService.create(user);
  // return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
  // }

  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(
      @PathVariable Long id,
      @RequestBody User user) {
    return userService.update(id, user)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

}
