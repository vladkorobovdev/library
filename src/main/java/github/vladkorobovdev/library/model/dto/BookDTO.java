package github.vladkorobovdev.library.model.dto;

import java.math.BigDecimal;

public record BookDTO(
    String title,
    String author,
    BigDecimal price) {
}
