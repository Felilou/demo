package at.spengergasse.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordDTO(
        @NotBlank(message = "Current password must not be blank")
        String newPassword,
        @NotBlank(message = "New password confirmation must not be blank")
        String newPasswordConfirmation
) {

}
