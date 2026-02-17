package at.spengergasse.playerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {
    private final PasswordEncoder passwordEncoder;
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerController(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, PlayerRepository playerRepository) {
        this.passwordEncoder = passwordEncoder;
        this.playerRepository = playerRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        try {
            if (signupRequest.username() == null || signupRequest.password() == null) {
                return ResponseEntity.badRequest().body("Username und Passwort dürfen nicht leer sein!");
            }
            Player player = new Player();
            player.setUsername(signupRequest.username());
            player.setPassword(passwordEncoder.encode(signupRequest.password()));
            playerRepository.save(player);

            // Automatisches Login nach erfolgreichem Signup
            Authentication auth = new UsernamePasswordAuthenticationToken(
                signupRequest.username(), signupRequest.password()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            return ResponseEntity.ok("Signup und Login erfolgreich!");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Username existiert bereits!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fehler beim Signup!");
        }
    }

}
