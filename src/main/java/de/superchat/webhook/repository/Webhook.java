package de.superchat.webhook.repository;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@NoArgsConstructor
@Entity
@Table(name = "webhook", schema = "public")
public class Webhook extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "source_id")
    private String sourceId = "AN";
    @Column(name = "created", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

}
