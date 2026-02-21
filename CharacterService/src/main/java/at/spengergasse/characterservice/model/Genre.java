package at.spengergasse.characterservice.model;

import java.util.*;

public enum Genre {
    Lovecraftian,
    Dnd,
    SciFi,
    Fantasy;

    private static final Map<Genre, List<String>> RACES = new HashMap<>();
    private static final Map<Genre, List<String>> PROFESSIONS = new HashMap<>();

    static {
        RACES.put(Lovecraftian, List.of("Deep One", "Cultist"));
        RACES.put(Dnd, List.of("Elf", "Zwerg"));
        RACES.put(SciFi, List.of("Alien", "Cyborg"));
        RACES.put(Fantasy, List.of("Ork", "Mensch"));

        PROFESSIONS.put(Lovecraftian, List.of("Priester", "Detektiv"));
        PROFESSIONS.put(Dnd, List.of("Krieger", "Magier"));
        PROFESSIONS.put(SciFi, List.of("Pilot", "Ingenieur"));
        PROFESSIONS.put(Fantasy, List.of("Bogenschütze", "Heiler"));
    }

    public List<String> getCharacterRaces() {
        return RACES.getOrDefault(this, Collections.emptyList());
    }

    public List<String> getProfessions() {
        return PROFESSIONS.getOrDefault(this, Collections.emptyList());
    }
}
