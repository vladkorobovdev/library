package github.vladkorobovdev.library.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import github.vladkorobovdev.library.model.entity.Order;
import github.vladkorobovdev.library.model.entity.OrderStatus;
import github.vladkorobovdev.library.repository.OrderRepository;
import github.vladkorobovdev.library.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @Mock
  private OrderRepository orderRepository;
  @Mock
  private PaymentRepository paymentRepository;
  @InjectMocks
  private PaymentService paymentService;

  @Test
  void createCheckoutSession_ShouldThrowException_WhenOrderIsAlreadyCompleted() {
    Long orderId = 1L;
    Order order = new Order();
    order.setId(orderId);
    order.setStatus(OrderStatus.COMPLETED);

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
      paymentService.createCheckoutSession(orderId);
    });

    assertEquals("This order is completed", exception.getMessage());
  }

  @Test
  void createCheckoutSession_ShouldThrowException_WhenOrderNotFound() {
    Long orderId = 99L;
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      paymentService.createCheckoutSession(orderId);
    });

    assertEquals("Order not found", exception.getMessage());
  }
}
