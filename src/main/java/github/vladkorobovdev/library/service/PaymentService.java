package github.vladkorobovdev.library.service;

import com.stripe.Stripe;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.vladkorobovdev.library.model.entity.*;
import github.vladkorobovdev.library.repository.OrderRepository;
import github.vladkorobovdev.library.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

  private final OrderRepository orderRepository;
  private final PaymentRepository paymentRepository;

  @Value("${stripe.api-key}")
  private String apiKey;

  @Value("${stripe.webhook-secret}")
  private String webhookSecret;

  @Value("${stripe.success-url}")
  private String successUrl;

  @Value("${stripe.cancel-url}")
  private String cancelUrl;

  public PaymentService(OrderRepository orderRepository, PaymentRepository paymentRepository) {
    this.orderRepository = orderRepository;
    this.paymentRepository = paymentRepository;
  }

  public String createCheckoutSession(Long orderId) throws Exception {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    if (order.getStatus() == OrderStatus.COMPLETED) {
      throw new IllegalStateException("This order is completed");
    }

    Stripe.apiKey = apiKey;

    BigDecimal total = order.getItems().stream()
        .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    long amount = total.multiply(BigDecimal.valueOf(100)).longValue();

    SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData
        .builder()
        .setName("Order " + order.getId())
        .build();

    SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
        .setCurrency("pln")
        .setUnitAmount(amount)
        .setProductData(productData)
        .build();

    SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
        .setQuantity(1L)
        .setPriceData(priceData)
        .build();

    SessionCreateParams params = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.PAYMENT)
        .setSuccessUrl(successUrl)
        .setCancelUrl(cancelUrl)
        .addLineItem(lineItem)
        .putMetadata("orderId", String.valueOf(order.getId()))
        .build();

    Session session = Session.create(params);

    Payment payment = paymentRepository.findByOrder_Id(order.getId())
        .orElseGet(() -> Payment.builder()
            .id(UUID.randomUUID().toString())
            .order(order)
            .createdAt(LocalDateTime.now().toString())
            .build());

    payment.setAmount(amount);
    payment.setCurrency("pln");
    payment.setStripeSessionId(session.getId());
    payment.setStatus(PaymentStatus.PENDING);
    paymentRepository.save(payment);

    return session.getUrl();
  }

  public void handleWebhook(String payload, String signature) {
    Stripe.apiKey = apiKey;
    Event event;

    try {
      event = Webhook.constructEvent(payload, signature, webhookSecret);
    } catch (SignatureVerificationException e) {
      throw new IllegalArgumentException("Invalid Stripe webhook signature.");
    }

    if (!"checkout.session.completed".equals(event.getType())) {
      return;
    }

    Session session;
    if (event.getDataObjectDeserializer().getObject().isPresent()) {
      session = (Session) event.getDataObjectDeserializer().getObject().get();
    } else {
      try {
        StripeObject stripeObject = event.getDataObjectDeserializer().deserializeUnsafe();
        if (stripeObject instanceof Session) {
          session = (Session) stripeObject;
        } else {
          throw new IllegalStateException("Deserialized object isn't a Stripe session");
        }
      } catch (EventDataObjectDeserializationException e) {
        throw new IllegalStateException("Error while unsafe Stripe session deserialization", e);
      }
    }

    if (session == null) {
      throw new IllegalStateException("Can't read Stripe session even with unsafe deserialization.");
    }

    String stripeSessionId = session.getId();
    Payment payment = paymentRepository.findByStripeSessionId(stripeSessionId)
        .orElseThrow(() -> new IllegalStateException("Payment for Stripe session not found"));

    if (payment.getStatus() == PaymentStatus.PAID) {
      return;
    }

    payment.setStatus(PaymentStatus.PAID);
    payment.setPaidAt(LocalDateTime.now().toString());
    paymentRepository.save(payment);

    Order order = payment.getOrder();
    if (order.getStatus() != OrderStatus.COMPLETED) {
      order.setStatus(OrderStatus.COMPLETED);
      orderRepository.save(order);
    }
  }
}
