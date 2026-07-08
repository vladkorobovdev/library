package github.vladkorobovdev.library.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.vladkorobovdev.library.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {
  private final CartService cartService;

  public CartController(CartService cartService) {
    this.cartService = cartService;
  }

  @PostMapping("/{bookId}")
  public ResponseEntity<String> addToCart(@PathVariable Long bookId, Principal principal) {
    cartService.addBookToCart(principal.getName(), bookId);
    return ResponseEntity.ok("Book added to cart");
  }

  @DeleteMapping("/{bookId}")
  public ResponseEntity<String> removeFromCart(@PathVariable Long bookId, Principal principal) {
    cartService.removeBookFromCart(principal.getName(), bookId);
    return ResponseEntity.ok("Book removed from cart");
  }
}
