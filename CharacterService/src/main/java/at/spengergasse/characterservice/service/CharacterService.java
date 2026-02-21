package at.spengergasse.characterservice.service;

import at.spengergasse.characterservice.persistance.CharacterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CharacterService {

    private final CharacterRepository characterRepository;

    public List<Character> loadAllCharactersByPlayerUuid(UUID playerUuid) {
        return characterRepository.loadAllCharactersByPlayerUuid(playerUuid);
    }

    public void loadCharacterByUuid(UUID characterUuid) {
        characterRepository.findByUuid(characterUuid)
                .orElseThrow(() -> new RuntimeException("Character not found: " + characterUuid));
    }

    public void CreateCharacter(Character character) {
        characterRepository.save(character);
    }

    public void deleteCharacterByUuid(UUID characterUuid) {
        characterRepository.delete(characterRepository.findByUuid(characterUuid).orElseThrow());
    }

}
