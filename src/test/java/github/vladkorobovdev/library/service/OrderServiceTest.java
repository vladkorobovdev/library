package github.vladkorobovdev.library.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import github.vladkorobovdev.library.model.entity.*;
import github.vladkorobovdev.library.repository.OrderRepository;
import github.vladkorobovdev.library.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private OrderService orderService;

  @Test
  void placeOrder_ShouldCreateOrderAndClearCart_WhenCartIsNotEmpty() {
    String username = "testuser";
    User user = new User();
    user.setLogin(username);
    user.setCartItems(new ArrayList<>());

    Book book = Book.builder().id(1L).title("Test Book").build();
    user.getCartItems().add(CartItem.builder().book(book).quantity(2).build());

    when(userRepository.findByLogin(username)).thenReturn(Optional.of(user));
    when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Order createdOrder = orderService.placeOrder(username);

    assertNotNull(createdOrder);
    assertEquals(OrderStatus.NEW, createdOrder.getStatus());
    assertEquals(1, createdOrder.getItems().size());
    assertEquals(2, createdOrder.getItems().get(0).getQuantity());

    assertTrue(user.getCartItems().isEmpty());
    verify(userRepository).save(user);
  }

  @Test
  void placeOrder_ShouldThrowException_WhenCartIsEmpty() {
    String username = "testuser";
    User user = new User();
    user.setLogin(username);
    user.setCartItems(new ArrayList<>());

    when(userRepository.findByLogin(username)).thenReturn(Optional.of(user));

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      orderService.placeOrder(username);
    });
    assertEquals("Cannot place order: Cart is empty", exception.getMessage());
  }
}
