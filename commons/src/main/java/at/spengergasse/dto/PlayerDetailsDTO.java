package at.spengergasse.dto;

import java.util.UUID;

public record PlayerDetailsDTO(
        String Username,
        UUID UUID
) {
}
