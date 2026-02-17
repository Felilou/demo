package at.spengergasse.playerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AccountController {
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountController(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/account")
    public String accountPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Player player = null;
        if (userDetails != null) {
            player = playerRepository.findByUsername(userDetails.getUsername()).orElse(null);
        }
        model.addAttribute("player", player);
        return "account";
    }

    @PostMapping("/account")
    public String updateAccount(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam(required = false) String username,
                                @RequestParam(required = false) String password,
                                @RequestParam(required = false) String passwordConfirm,
                                Model model) {
        Player player = null;
        boolean usernameConflict = false;
        boolean passwordMismatch = false;
        boolean usernameSuccess = false;
        boolean passwordSuccess = false;
        if (userDetails != null) {
            player = playerRepository.findByUsername(userDetails.getUsername()).orElse(null);
            if (player != null) {
                // Username ändern, wenn ein neuer Wert übergeben wurde und er sich unterscheidet
                if (username != null && !username.isBlank() && !username.equals(player.getUsername())) {
                    Player existing = playerRepository.findByUsername(username).orElse(null);
                    if (existing != null) {
                        usernameConflict = true;
                    } else {
                        player.setUsername(username);
                        usernameSuccess = true;
                    }
                }
                // Passwort ändern, wenn beide Felder ausgefüllt und gleich sind
                if (password != null && !password.isBlank()) {
                    if (passwordConfirm != null && password.equals(passwordConfirm)) {
                        player.setPassword(passwordEncoder.encode(password));
                        passwordSuccess = true;
                    } else {
                        passwordMismatch = true;
                    }
                }
                // Nur speichern, wenn sich etwas geändert hat
                if (usernameSuccess || passwordSuccess) {
                    playerRepository.save(player);
                }
            }
        }
        model.addAttribute("player", player);
        model.addAttribute("usernameConflict", usernameConflict);
        model.addAttribute("passwordMismatch", passwordMismatch);
        model.addAttribute("usernameSuccess", usernameSuccess);
        model.addAttribute("passwordSuccess", passwordSuccess);
        return "account";
    }
}
