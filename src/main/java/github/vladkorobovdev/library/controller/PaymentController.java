package github.vladkorobovdev.library.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import github.vladkorobovdev.library.service.PaymentService;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping("/checkout/{orderId}")
  public ResponseEntity<Map<String, String>> createCheckoutSession(@PathVariable Long orderId) {
    try {
      String url = paymentService.createCheckoutSession(orderId);
      return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("url", url));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/webhook")
  public ResponseEntity<Void> handleWebhook(
      @RequestBody String payload,
      @RequestHeader("Stripe-Signature") String signature) {
    paymentService.handleWebhook(payload, signature);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/success")
  public ResponseEntity<String> success() {
    return ResponseEntity.ok("success");
  }

  @GetMapping("/cancel")
  public ResponseEntity<String> cancel() {
    return ResponseEntity.ok("cancel");
  }
}
