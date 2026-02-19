package at.spengergasse.playerservice.webcontroller;

import at.spengergasse.dto.SignupDTO;
import at.spengergasse.playerservice.model.Player;
import at.spengergasse.playerservice.persistance.PlayerRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final PlayerRepository playerRepository;

    @GetMapping("/login")
    public String loginPage(@org.springframework.web.bind.annotation.RequestParam(value = "error", required = false) String error,
                            @org.springframework.web.bind.annotation.RequestParam(value = "logout", required = false) String logout,
                            @org.springframework.web.bind.annotation.RequestParam(value = "signupSuccess", required = false) String signupSuccess,
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
    public String signupForm(Model model) {
        model.addAttribute("signupDTO", new SignupDTO("", "", ""));
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("signupDTO") SignupDTO signupDTO,
                         BindingResult bindingResult,
                         Model model) {
        boolean signupConflict = false;
        boolean signupMismatch = false;
        boolean signupError = false;
        List<String> formErrors = null;

        if (bindingResult.hasErrors()) {
            formErrors = bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.toList());
        } else if (!isPasswordConfirmationValid(signupDTO)) {
            signupMismatch = true;
        } else if (!isUsernameAvailable(signupDTO.username())) {
            signupConflict = true;
        } else {
            try {
                createPlayer(signupDTO);
                return "redirect:/auth/login?signupSuccess";
            } catch (Exception e) {
                signupError = true;
            }
        }
        model.addAttribute("signupConflict", signupConflict);
        model.addAttribute("signupMismatch", signupMismatch);
        model.addAttribute("signupError", signupError);
        model.addAttribute("signupFormErrors", formErrors);
        return "signup";
    }

    // Hilfsmethoden
    private boolean isUsernameAvailable(String username) {
        return playerRepository.findByUsername(username).isEmpty();
    }

    private boolean isPasswordConfirmationValid(SignupDTO dto) {
        return dto.password().equals(dto.passwordConfirmation());
    }

    private void createPlayer(SignupDTO dto) {
        Player player = new Player();
        player.setUsername(dto.username());
        player.setPassword(passwordEncoder.encode(dto.password()));
        playerRepository.save(player);
    }
}
