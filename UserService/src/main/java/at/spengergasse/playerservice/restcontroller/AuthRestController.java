package at.spengergasse.playerservice.restcontroller;

import at.spengergasse.dto.PlayerDetailsDTO;
import at.spengergasse.playerservice.model.Player;
import at.spengergasse.playerservice.service.AuthService;
import jakarta.ws.rs.core.SecurityContext;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthRestController {
    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<Player> getMe() {
        return ResponseEntity.ok(authService.getMe());
    }

    @GetMapping("/{UUID}")
    public ResponseEntity<PlayerDetailsDTO> getPlayerDetails(@PathVariable String UUID) {
        return ResponseEntity.ok(authService.getPlayerDetails(UUID));
    }
}
