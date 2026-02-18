package at.spengergasse.playerservice.model;

import at.spengergasse.model.BaseEntity;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Player extends BaseEntity {

    @NotNull
    private String username;

    @NotNull
    @NotBlank
    private String password;

}
