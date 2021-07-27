package de.superchat.user.repository;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "source", schema = "public")
@Entity
public class Source {

    public enum AsEnum {
        /**
         * Superchat
         */
        SC,
        /**
         * Facebook
         */
        FB,
        /**
         * Telegram
         */
        TG,
        /**
         * Gmail
         */
        GM,
        /**
         * Anonymous webhook
         */
        AN
    }

    @Id
    private String id;
    private String description;
    @OneToMany(mappedBy = "source", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<User> users;
}
