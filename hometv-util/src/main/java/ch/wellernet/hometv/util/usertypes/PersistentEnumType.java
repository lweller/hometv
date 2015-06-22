/**
 *
 */
package ch.wellernet.hometv.util.usertypes;

import static org.apache.commons.lang3.reflect.MethodUtils.invokeExactStaticMethod;
import static org.hibernate.internal.util.ReflectHelper.classForName;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StringType;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import ch.wellernet.hometv.util.model.PersistentEnum;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public class PersistentEnumType implements UserType, ParameterizedType {

    /**
     *
     */
    private static final String ENUM_CLASS = "enumClass";
    private Class<? extends PersistentEnum> enumClass;

    /**
     * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, java.lang.Object)
     */
    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    /**
     * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
     */
    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    /**
     * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
     */
    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    /**
     * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    /**
     * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
     */
    @Override
    public int hashCode(Object x) throws HibernateException {
        return Objects.hashCode(x);
    }

    /**
     * @see org.hibernate.usertype.UserType#isMutable()
     */
    @Override
    public boolean isMutable() {
        return false;
    }

    /**
     * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], org.hibernate.engine.spi.SessionImplementor,
     *      java.lang.Object)
     */
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        String value = (String) StringType.INSTANCE.nullSafeGet(rs, names, session, owner);
        if (value == null) {
            return null;
        } else {
            try {
                return invokeExactStaticMethod(enumClass, "valueForPersistentValue", value);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
                throw new HibernateException(exception);
            }
        }
    }

    /**
     * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int,
     *      org.hibernate.engine.spi.SessionImplementor)
     */
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        StringType.INSTANCE.nullSafeSet(st, value == null ? null : ((PersistentEnum) value).toPersistetnValue(), index, session);
    }

    /**
     * @see org.hibernate.usertype.UserType#replace(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    /**
     * @see org.hibernate.usertype.UserType#returnedClass()
     */
    @Override
    public Class<File> returnedClass() {
        return File.class;
    }

    /**
     * @see org.hibernate.usertype.ParameterizedType#setParameterValues(java.util.Properties)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setParameterValues(Properties parameters) {
        try {
            enumClass = classForName(parameters.getProperty(ENUM_CLASS), this.getClass()).asSubclass(PersistentEnum.class);
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * @see org.hibernate.usertype.UserType#sqlTypes()
     */
    @Override
    public int[] sqlTypes() {
        return new int[] { StringType.INSTANCE.sqlType() };
    }
}
