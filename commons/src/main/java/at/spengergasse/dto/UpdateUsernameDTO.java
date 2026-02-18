package at.spengergasse.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUsernameDTO(
        @NotBlank(message = "Username must not be blank")
        String username
) {
}
