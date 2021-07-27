package de.superchat.user.repository;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_info", schema = "public")
public class UserInfo extends PanacheEntityBase {

    @Id
    private UUID userId;
    private String firstname;
    private String lastname;
    @Column(name = "created", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

}
