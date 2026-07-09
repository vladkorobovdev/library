package github.vladkorobovdev.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import github.vladkorobovdev.library.model.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findAllByUserId(Long userId);
}
