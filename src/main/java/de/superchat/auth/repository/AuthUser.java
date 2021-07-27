package de.superchat.auth.repository;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user", schema = "public")
@Where(clause = "source = 'SC' and active = true")
public class AuthUser extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    private String username;
    private String email;
    private String salt;
    private String password;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_role",
        joinColumns = {@JoinColumn(name = "user_id")},
        inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<AuthRole> roles;

    public static AuthUser findByUsernameOrEmail(String usernameOrEmail) {
        return AuthUser.find("username = ?1", usernameOrEmail)
            .firstResult();
    }

}
