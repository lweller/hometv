package ch.wellernet.hometv.util.dao;

import ch.wellernet.hometv.util.model.IdentifyableObject;

public class IntegerIdGenerator<T extends IdentifyableObject<Integer>> implements IdGenerator<Integer, T> {
    private int nextId;

    public IntegerIdGenerator() {
        this.nextId = 1;
    }

    @Override
    public Integer generateId() {
        return nextId++;
    }

    @Override
    public boolean requiresNewId(T object) {
        return object.getId() == null;
    }

}
