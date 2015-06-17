package ch.wellernet.hometv.util.model;

import java.io.Serializable;

public abstract class IdentifyableObject<ID> implements Serializable {
    private static final long serialVersionUID = 1L;

    private ID id;

    public final ID getId() {
        return id;
    }
}
