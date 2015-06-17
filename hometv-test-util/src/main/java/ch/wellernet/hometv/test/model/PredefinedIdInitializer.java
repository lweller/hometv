/**
 *
 */
package ch.wellernet.hometv.test.model;

import java.lang.reflect.Field;

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
            Field field = IdentifyableObject.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(object, id);
            used = true;
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
