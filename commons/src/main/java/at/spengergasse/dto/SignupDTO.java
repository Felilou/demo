package at.spengergasse.dto;

import jakarta.validation.constraints.NotBlank;

public record SignupDTO(
        @NotBlank(message = "Username must not be blank")
        String username,
        @NotBlank(message = "Password must not be blank")
        String password,
        @NotBlank(message = "Password confirmation must not be blank")
        String passwordConfirmation
) {
}
