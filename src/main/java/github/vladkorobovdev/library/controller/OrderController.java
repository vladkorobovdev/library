package github.vladkorobovdev.library.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import github.vladkorobovdev.library.model.entity.Order;
import github.vladkorobovdev.library.model.entity.OrderStatus;
import github.vladkorobovdev.library.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  public ResponseEntity<String> placeOrder(Principal principal) {
    try {
      orderService.placeOrder(principal.getName());
      return ResponseEntity.ok("Order placed successfully");
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping("/my")
  public ResponseEntity<List<Order>> getMyOrders(Principal principal) {
    return ResponseEntity.ok(orderService.getUserOrders(principal.getName()));
  }

  @GetMapping
  public ResponseEntity<List<Order>> getAllOrders() {
    return ResponseEntity.ok(orderService.getAllOrders());
  }

  @PutMapping("/{id}/status")
  public ResponseEntity<Order> updateOrderStatus(
      @PathVariable Long id,
      @RequestParam OrderStatus status) {
    return ResponseEntity.ok(orderService.changeOrderStatus(id, status));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
    try {
      orderService.deleteOrder(id);
      return ResponseEntity.ok("Order deleted successfully");
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
