package at.spengergasse.playerservice.webcontroller;

import at.spengergasse.playerservice.model.Player;
import at.spengergasse.playerservice.persistance.PlayerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final PlayerRepository playerRepository;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "signupSuccess", required = false) String signupSuccess,
                            Model model) {
        if (error != null) model.addAttribute("error", true);
        if (logout != null) model.addAttribute("logout", true);
        if (signupSuccess != null) model.addAttribute("signupSuccess", true);
        return "login";
    }

    @GetMapping("/logout")
    public String logoutPage() {
        return "redirect:/auth/login?logout";
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String passwordConfirm,
                         Model model) {

        boolean signupConflict = false;
        boolean signupMismatch = false;
        boolean signupError = false;

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            signupError = true;
        } else if (!password.equals(passwordConfirm)) {
            signupMismatch = true;
        } else if (playerRepository.findByUsername(username).isPresent()) {
            signupConflict = true;
        } else {
            try {
                Player player = new Player();
                player.setUsername(username);
                player.setPassword(passwordEncoder.encode(password));
                playerRepository.save(player);

                return "redirect:/auth/login?signupSuccess";
            } catch (Exception e) {
                signupError = true;
            }
        }

        model.addAttribute("signupConflict", signupConflict);
        model.addAttribute("signupMismatch", signupMismatch);
        model.addAttribute("signupError", signupError);
        return "signup";
    }

}
