package at.spengergasse.playerservice.service;

import at.spengergasse.dto.PlayerDetailsDTO;
import at.spengergasse.playerservice.model.Player;
import at.spengergasse.playerservice.persistance.PlayerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final PlayerRepository playerRepository;

    public Player getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("No authentication found");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        return playerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public PlayerDetailsDTO getPlayerDetails(String uuid) {

        Player player = playerRepository.findByUuid(java.util.UUID.fromString(uuid))
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new PlayerDetailsDTO(player.getUsername(), player.getUuid());
    }
}
