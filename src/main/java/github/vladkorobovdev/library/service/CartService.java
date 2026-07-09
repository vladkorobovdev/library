package github.vladkorobovdev.library.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import github.vladkorobovdev.library.model.entity.Book;
import github.vladkorobovdev.library.model.entity.CartItem;
import github.vladkorobovdev.library.model.entity.User;
import github.vladkorobovdev.library.repository.BookRepository;
import github.vladkorobovdev.library.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class CartService {
  private final UserRepository userRepository;
  private final BookRepository bookRepository;

  public CartService(UserRepository userRepository, BookRepository bookRepository) {
    this.userRepository = userRepository;
    this.bookRepository = bookRepository;
  }

  @Transactional
  public void addBookToCart(String userLogin, Long bookId) {
    User user = userRepository.findByLogin(userLogin)
        .orElseThrow(() -> new RuntimeException("User not found"));
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new RuntimeException("Book not found"));

    Optional<CartItem> existingItem = user.getCartItems().stream()
        .filter(item -> item.getBook().getId().equals(bookId))
        .findFirst();

    if (existingItem.isPresent()) {
      CartItem item = existingItem.get();
      item.setQuantity(item.getQuantity() + 1);
    } else {
      CartItem newItem = CartItem.builder()
          .user(user)
          .book(book)
          .quantity(1)
          .build();
      user.getCartItems().add(newItem);
    }

    userRepository.save(user);
  }

  @Transactional
  public List<CartItem> getUserCart(String userLogin) {
    User user = userRepository.findByLogin(userLogin)
        .orElseThrow(() -> new RuntimeException("User not found"));
    return user.getCartItems();
  }

  @Transactional
  public void removeBookFromCart(String userLogin, Long bookId) {
    User user = userRepository.findByLogin(userLogin)
        .orElseThrow(() -> new RuntimeException("User not found"));

    Optional<CartItem> existingItem = user.getCartItems().stream()
        .filter(item -> item.getBook().getId().equals(bookId))
        .findFirst();

    if (existingItem.isPresent()) {
      CartItem item = existingItem.get();
      if (item.getQuantity() > 1) {
        item.setQuantity(item.getQuantity() - 1);
      } else {
        user.getCartItems().remove(item);
      }
      userRepository.save(user);
    }
  }
}
