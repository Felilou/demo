package at.spengergasse.characterservice.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {

    @Query("SELECT c FROM Character c WHERE c.playerUuid = :playerUuid")
    List<Character> loadAllCharactersByPlayerUuid(UUID playerUuid);

    @Query("SELECT c FROM Character c WHERE c.uuid = :uuid")
    Optional<Character> findByUuid(UUID uuid);

}
