package github.vladkorobovdev.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import github.vladkorobovdev.library.model.entity.Payment;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
  Optional<Payment> findByStripeSessionId(String stripeSessionId);

  Optional<Payment> findByOrder_Id(Long orderId);
}
