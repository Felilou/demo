package at.spengergasse.characterservice.model;

import at.spengergasse.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Character extends BaseEntity {

    @Enumerated(EnumType.STRING)
    Genre genre;

    String name;

    UUID playerUuid;

}
