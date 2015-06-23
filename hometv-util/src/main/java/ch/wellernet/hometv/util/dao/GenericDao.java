package ch.wellernet.hometv.util.dao;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ch.wellernet.hometv.util.model.IdentifyableObject;
import ch.wellernet.hometv.util.model.ModelObjectRepository;

@Transactional(propagation = MANDATORY)
public interface GenericDao<ID, T extends IdentifyableObject<ID>> extends ModelObjectRepository<ID, T> {

    @Override
    public void attach(T object);

    public void delete(ID id);

    public T find(ID id);

    public List<T> findAll();
}