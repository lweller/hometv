package ch.wellernet.hometv.util.dao;

import java.util.List;

import ch.wellernet.hometv.util.model.IdentifyableObject;
import ch.wellernet.hometv.util.model.ModelObjectRepository;

public interface GenericDao<ID, T extends IdentifyableObject<ID>> extends ModelObjectRepository<ID, T> {

    @Override
    public void attach(T object);

    public void delete(ID id);

    public T find(ID id);

    public List<T> findAll();
}