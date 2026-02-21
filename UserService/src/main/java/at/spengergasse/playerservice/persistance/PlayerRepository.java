package at.spengergasse.playerservice.persistance;

import at.spengergasse.playerservice.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByUsername(String username);
    Optional<Player> findByUuid(UUID uuid);
}
