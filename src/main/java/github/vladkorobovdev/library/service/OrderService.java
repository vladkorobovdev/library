package github.vladkorobovdev.library.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.vladkorobovdev.library.model.entity.Order;
import github.vladkorobovdev.library.model.entity.OrderItem;
import github.vladkorobovdev.library.model.entity.OrderStatus;
import github.vladkorobovdev.library.model.entity.User;
import github.vladkorobovdev.library.repository.OrderRepository;
import github.vladkorobovdev.library.repository.UserRepository;

@Service
public class OrderService {
  private final OrderRepository orderRepository;
  private final UserRepository userRepository;

  public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
    this.orderRepository = orderRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public Order placeOrder(String username) {
    User user = userRepository.findByLogin(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    if (user.getCartItems().isEmpty()) {
      throw new RuntimeException("Cannot place order: Cart is empty");
    }

    Order order = Order.builder()
        .user(user)
        .status(OrderStatus.NEW)
        .createdAt(LocalDateTime.now())
        .build();

    List<OrderItem> orderItems = user.getCartItems().stream().map(cartItem -> OrderItem.builder()
        .order(order)
        .book(cartItem.getBook())
        .quantity(cartItem.getQuantity())
        .build()).toList();

    order.setItems(orderItems);
    Order savedOrder = orderRepository.save(order);

    user.getCartItems().clear();
    userRepository.save(user);

    return savedOrder;
  }

  @Transactional
  public void deleteOrder(Long orderId) {
    if (!orderRepository.existsById(orderId)) {
      throw new RuntimeException("Order not found");
    }
    orderRepository.deleteById(orderId);
  }

  @Transactional
  public Order changeOrderStatus(Long orderId, OrderStatus newStatus) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new RuntimeException("Order not found"));

    order.setStatus(newStatus);
    return orderRepository.save(order);
  }

  @Transactional(readOnly = true)
  public List<Order> getUserOrders(String username) {
    User user = userRepository.findByLogin(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
    return orderRepository.findAllByUserId(user.getId());
  }

  @Transactional(readOnly = true)
  public List<Order> getAllOrders() {
    return orderRepository.findAll();
  }
}
