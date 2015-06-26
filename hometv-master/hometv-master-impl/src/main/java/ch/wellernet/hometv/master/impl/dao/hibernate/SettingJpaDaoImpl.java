/**
 *
 */
package ch.wellernet.hometv.master.impl.dao.hibernate;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import ch.wellernet.hibernate.dao.AbstractJpaDaoImpl;
import ch.wellernet.hometv.master.impl.dao.SettingDao;
import ch.wellernet.hometv.master.impl.model.Setting;
import ch.wellernet.hometv.master.impl.model.Setting_;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Repository
public class SettingJpaDaoImpl extends AbstractJpaDaoImpl<Integer, Setting> implements SettingDao {

    /**
     * @see ch.wellernet.hometv.master.impl.dao.SettingDao#findByName(java.lang.String)
     */
    @Override
    public Setting findByName(String name) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Setting> query = criteriaBuilder.createQuery(getType());
        Root<Setting> setting = query.from(getType());
        query = query.where(criteriaBuilder.equal(setting.get(Setting_.name), name));
        try {
            return entityManager.createQuery(query).getSingleResult();
        } catch (NoResultException exception) {
            return null;
        }
    }

    /**
     * @see ch.wellernet.hibernate.dao.AbstractJpaDaoImpl#getType()
     */
    @Override
    protected Class<Setting> getType() {
        return Setting.class;
    }
}
