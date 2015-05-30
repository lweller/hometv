package ch.wellernet.hometv.util.model;

import java.io.Serializable;

public abstract class IdentifyableObject<ID> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final ID id;

    public IdentifyableObject(ID id) {
        this.id = id;
    }

    public ID getId() {
        return id;
    }

}
