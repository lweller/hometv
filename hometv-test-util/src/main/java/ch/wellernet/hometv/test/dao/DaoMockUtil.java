/**
 *
 */
package ch.wellernet.hometv.test.dao;

import static org.apache.commons.lang3.reflect.FieldUtils.writeField;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;

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
                    writeField(invocation.getArgumentAt(0, Object.class), "id", id, true);
                } catch (IllegalAccessException e) {
                    // should never happen as we force access
                    throw new RuntimeException(e);
                }
                return null;
            }
        }).when(dao).attach((T) any());
    }

    private DaoMockUtil() {
    }
}
