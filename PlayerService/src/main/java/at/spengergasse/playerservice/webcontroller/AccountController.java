package at.spengergasse.playerservice.webcontroller;

import at.spengergasse.playerservice.model.Player;
import at.spengergasse.playerservice.persistance.PlayerRepository;
import lombok.AllArgsConstructor;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/auth")
@AllArgsConstructor
public class AccountController {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

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
                // Username ändern
                if (username != null && !username.isBlank() && !username.equals(player.getUsername())) {
                    Player existing = playerRepository.findByUsername(username).orElse(null);
                    if (existing != null) {
                        usernameConflict = true;
                    } else {
                        player.setUsername(username);
                        usernameSuccess = true;
                        playerRepository.save(player); // Speichere zuerst den neuen Username
                        // SecurityContext auf neuen Username aktualisieren
                        UserDetails updatedUser = org.springframework.security.core.userdetails.User
                            .withUsername(username)
                            .password(player.getPassword())
                            .authorities(userDetails.getAuthorities())
                            .build();
                        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                            updatedUser,
                            player.getPassword(),
                            updatedUser.getAuthorities()
                        );
                        SecurityContextHolder.getContext().setAuthentication(newAuth);
                    }
                }
                // Passwort ändern
                if (password != null && !password.isBlank()) {
                    if (password.equals(passwordConfirm)) {
                        player.setPassword(passwordEncoder.encode(password));
                        passwordSuccess = true;
                    } else {
                        passwordMismatch = true;
                    }
                }
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
