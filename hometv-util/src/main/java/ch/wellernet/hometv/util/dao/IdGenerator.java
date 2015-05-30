package ch.wellernet.hometv.util.dao;

public interface IdGenerator<ID, T> {
    public ID generateId();

    public boolean requiresNewId(T object);
}