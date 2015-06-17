/**
 *
 */
package ch.wellernet.hometv.test.dao;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;

import java.lang.reflect.Field;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ch.wellernet.hometv.util.dao.GenericDao;
import ch.wellernet.hometv.util.model.IdentifyableObject;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public class DaoMockUtil {

    @SuppressWarnings("unchecked")
    public static <ID, T extends IdentifyableObject<ID>> void assignIdWhenAttached(GenericDao<ID, T> dao, final ID id) {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                try {
                    Field field = IdentifyableObject.class.getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(invocation.getArguments()[0], id);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                } catch (SecurityException e) {
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        }).when(dao).attach((T) any());
    }

    private DaoMockUtil() {
    }
}
