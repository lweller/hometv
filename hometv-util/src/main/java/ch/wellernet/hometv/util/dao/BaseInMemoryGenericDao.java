package ch.wellernet.hometv.util.dao;

import static java.util.Collections.unmodifiableList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.wellernet.hometv.util.model.IdentifyableObject;

public class BaseInMemoryGenericDao<ID, T extends IdentifyableObject<ID>> implements GenericDao<ID, T> {
    private final Map<ID, T> objects;
    private final IdGenerator<ID, T> idGenerator;

    public BaseInMemoryGenericDao(IdGenerator<ID, T> idGenerator) {
        this.objects = new HashMap<ID, T>();
        this.idGenerator = idGenerator;
    }

    @Override
    public void attach(T object) {
        initId(object);
        objects.put(object.getId(), object);
    }

    @Override
    public void delete(ID id) {
        objects.remove(id);
    }

    @Override
    public T find(ID id) {
        return objects.get(id);
    }

    @Override
    public List<T> findAll() {
        return unmodifiableList(new ArrayList<T>(objects.values()));
    }

    private void initId(T object) {
        if (idGenerator.requiresNewId(object)) {
            try {
                Field field = IdentifyableObject.class.getDeclaredField("id");
                field.setAccessible(true);
                field.set(object, idGenerator.generateId());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
