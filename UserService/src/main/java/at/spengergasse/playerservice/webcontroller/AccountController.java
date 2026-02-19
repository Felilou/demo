package at.spengergasse.playerservice.webcontroller;

import at.spengergasse.dto.UpdatePasswordDTO;
import at.spengergasse.dto.UpdateUsernameDTO;
import at.spengergasse.playerservice.model.Player;
import at.spengergasse.playerservice.persistance.PlayerRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/auth")
@AllArgsConstructor
public class AccountController {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/account")
    public String accountPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Player player = findCurrentPlayer(userDetails);
        model.addAttribute("player", player);
        model.addAttribute("updateUsernameDTO", new UpdateUsernameDTO(player != null ? player.getUsername() : ""));
        model.addAttribute("updatePasswordDTO", new UpdatePasswordDTO("", ""));
        return "account";
    }

    @PostMapping(value = "/account", params = "updateUsername")
    public String updateUsername(@AuthenticationPrincipal UserDetails userDetails,
                                @Valid @ModelAttribute("updateUsernameDTO") UpdateUsernameDTO updateUsernameDTO,
                                BindingResult bindingResult,
                                Model model) {
        Player player = findCurrentPlayer(userDetails);
        boolean usernameConflict = false;
        boolean usernameSuccess = false;
        List<String> formErrors = null;
        if (bindingResult.hasErrors()) {
            formErrors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
        } else if (player != null) {
            if (!updateUsernameDTO.username().equals(player.getUsername())) {
                if (!isUsernameAvailable(updateUsernameDTO.username())) {
                    usernameConflict = true;
                } else {
                    updatePlayerUsername(player, updateUsernameDTO.username());
                    usernameSuccess = true;
                    updateSecurityContextUsername(userDetails, player);
                }
            }
        }
        model.addAttribute("player", player);
        model.addAttribute("updateUsernameDTO", updateUsernameDTO);
        model.addAttribute("updatePasswordDTO", new UpdatePasswordDTO("", ""));
        model.addAttribute("usernameConflict", usernameConflict);
        model.addAttribute("usernameSuccess", usernameSuccess);
        model.addAttribute("usernameFormErrors", formErrors);
        return "account";
    }

    @PostMapping(value = "/account", params = "updatePassword")
    public String updatePassword(@AuthenticationPrincipal UserDetails userDetails,
                                @Valid @ModelAttribute("updatePasswordDTO") UpdatePasswordDTO updatePasswordDTO,
                                BindingResult bindingResult,
                                Model model) {
        Player player = findCurrentPlayer(userDetails);
        boolean passwordMismatch = false;
        boolean passwordSuccess = false;
        List<String> formErrors = null;
        if (bindingResult.hasErrors()) {
            formErrors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
        } else if (player != null) {
            if (isPasswordConfirmationValid(updatePasswordDTO)) {
                updatePlayerPassword(player, updatePasswordDTO.newPassword());
                passwordSuccess = true;
            } else {
                passwordMismatch = true;
            }
        }
        model.addAttribute("player", player);
        model.addAttribute("updateUsernameDTO", new UpdateUsernameDTO(player != null ? player.getUsername() : ""));
        model.addAttribute("updatePasswordDTO", updatePasswordDTO);
        model.addAttribute("passwordMismatch", passwordMismatch);
        model.addAttribute("passwordSuccess", passwordSuccess);
        model.addAttribute("passwordFormErrors", formErrors);
        return "account";
    }

    // Hilfsmethoden
    private Player findCurrentPlayer(UserDetails userDetails) {
        if (userDetails == null) return null;
        return playerRepository.findByUsername(userDetails.getUsername()).orElse(null);
    }

    private boolean isUsernameAvailable(String username) {
        return playerRepository.findByUsername(username).isEmpty();
    }

    private void updatePlayerUsername(Player player, String newUsername) {
        player.setUsername(newUsername);
        playerRepository.save(player);
    }

    private void updateSecurityContextUsername(UserDetails oldUserDetails, Player player) {
        UserDetails updatedUser = org.springframework.security.core.userdetails.User
                .withUsername(player.getUsername())
                .password(player.getPassword())
                .authorities(oldUserDetails.getAuthorities())
                .build();
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                updatedUser,
                player.getPassword(),
                updatedUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    private boolean isPasswordConfirmationValid(UpdatePasswordDTO dto) {
        return dto.newPassword().equals(dto.newPasswordConfirmation());
    }

    private void updatePlayerPassword(Player player, String newPassword) {
        player.setPassword(passwordEncoder.encode(newPassword));
        playerRepository.save(player);
    }
}
