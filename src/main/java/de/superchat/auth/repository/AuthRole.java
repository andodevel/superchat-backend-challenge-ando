package de.superchat.auth.repository;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "role", schema = "public")
@Entity
public class AuthRole {

    public enum AsEnum {
        ADMIN,
        USER
    }

    @Id
    private String id;
    private String description;
}
