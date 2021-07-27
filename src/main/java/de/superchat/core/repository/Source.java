package de.superchat.core.repository;

import javax.persistence.Entity;
import javax.persistence.Id;
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
}
