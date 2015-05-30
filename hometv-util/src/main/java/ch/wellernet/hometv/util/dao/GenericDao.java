package ch.wellernet.hometv.util.dao;

import java.util.List;

import ch.wellernet.hometv.util.model.IdentifyableObject;

public interface GenericDao<ID, T extends IdentifyableObject<ID>> {

    public void delete(ID id);

    public T find(ID id);

    public List<T> findAll();

    public T save(T object);
}