package github.vladkorobovdev.library.model.dto;

public record SignupRequest(
    String login,
    String firstName,
    String lastName,
    String password) {
}
