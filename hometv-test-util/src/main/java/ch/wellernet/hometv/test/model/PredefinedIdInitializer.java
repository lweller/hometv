/**
 *
 */
package ch.wellernet.hometv.test.model;

import static org.apache.commons.lang3.reflect.FieldUtils.writeField;
import ch.wellernet.hometv.util.model.IdentifyableObject;
import ch.wellernet.hometv.util.model.ModelObjectRepository;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public class PredefinedIdInitializer<T extends IdentifyableObject<Integer>> implements ModelObjectRepository<Integer, T> {

    private boolean used;
    private int id;

    public PredefinedIdInitializer(int id) {
        this.id = id;
        this.used = false;
    }

    /**
     * @see ch.wellernet.hometv.util.model.ModelObjectRepository#attach(ch.wellernet.hometv.util.model.IdentifyableObject)
     */
    @Override
    public void attach(T object) {
        initId(object);
    }

    private void initId(T object) {
        if (used) {
            throw new IllegalStateException("this initializer instance has already been used");
        }
        try {
            writeField(object, "id", id, true);
            used = true;
        } catch (IllegalAccessException e) {
            // should never happen as we force access
            throw new RuntimeException(e);
        }
    }
}
