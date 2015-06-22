package ch.wellernet.hometv.util.dao;

import static java.util.Collections.unmodifiableList;
import static org.apache.commons.lang3.reflect.FieldUtils.writeField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.wellernet.hometv.util.model.IdentifyableObject;

public class BaseInMemoryGenericDao<ID, T extends IdentifyableObject<ID>> implements GenericDao<ID, T> {
    private static final Log LOG = LogFactory.getLog(BaseInMemoryGenericDao.class);

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
                writeField(object, "id", idGenerator.generateId(), true);
            } catch (IllegalAccessException exception) {
                // should never happen as we force access
                LOG.debug("Caught exception", exception);
                throw new RuntimeException(exception);
            }
        }
    }
}
