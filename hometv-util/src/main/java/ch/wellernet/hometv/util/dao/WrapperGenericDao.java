package ch.wellernet.hometv.util.dao;

import java.util.List;

import ch.wellernet.hometv.util.model.IdentifyableObject;

public class WrapperGenericDao<ID, T extends IdentifyableObject<ID>> implements GenericDao<ID, T> {

    private final GenericDao<ID, T> targetDao;

    public WrapperGenericDao(GenericDao<ID, T> targetDao) {
        this.targetDao = targetDao;
    }

    @Override
    public void attach(T object) {
        targetDao.attach(object);
    }

    @Override
    public void delete(ID id) {
        targetDao.delete(id);
    }

    @Override
    public T find(ID id) {
        return targetDao.find(id);
    }

    @Override
    public List<T> findAll() {
        return targetDao.findAll();
    }
}
