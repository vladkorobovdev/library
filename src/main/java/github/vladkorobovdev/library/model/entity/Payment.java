package github.vladkorobovdev.library.model.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
  @Id
  @Column(nullable = false, unique = true)
  private String id;

  @JsonIgnore
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "order_id", nullable = false, unique = true)
  private Order order;

  @Column(nullable = false)
  private Long amount;

  @Column(nullable = false)
  private String currency;

  @Column(name = "stripe_session_id", unique = true)
  private String stripeSessionId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus status;

  @Column(name = "created_at", nullable = false)
  private String createdAt;

  @Column(name = "paid_at")
  private String paidAt;
}
