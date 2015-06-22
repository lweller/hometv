package ch.wellernet.hometv.util.model;

import static javax.persistence.GenerationType.SEQUENCE;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class IdentifyableObject<ID> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "primary_key")
    private ID id;

    @Version
    @Column(name = "LAST_MODIFICATION")
    private Timestamp lastModifaction;

    public ID getId() {
        return id;
    }
}
