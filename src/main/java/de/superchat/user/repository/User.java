package de.superchat.user.repository;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import javax.persistence.Entity;

@Entity
@UserDefinition
public class User extends PanacheEntity {

    public static final String PEPPER = "superchat";

    @Username
    public String username;

    @Password
    public String password;


    /**
     * Adds a new user in the database
     *
     * @param username the user name
     * @param password the unencrypted password (it will be encrypted with bcrypt)
     * @param role     the comma-separated roles
     */
    public static void add(String username, String password, String role) {
        User user = new User();
        user.username = username;
        user.password = BcryptUtil.bcryptHash(password);
        user.persist();
    }
}
