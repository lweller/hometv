/**
 *
 */
package ch.wellernet.hometv.master.impl.dao.hibernate;

import java.io.File;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import ch.wellernet.hibernate.dao.AbstractJpaDaoImpl;
import ch.wellernet.hometv.master.api.model.PlayListItem;
import ch.wellernet.hometv.master.api.model.PlayListItem_;
import ch.wellernet.hometv.master.impl.dao.PlayListItemDao;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Repository
public class PlayListItemJpaDaoImpl extends AbstractJpaDaoImpl<Integer, PlayListItem> implements PlayListItemDao {

    /**
     * @see ch.wellernet.hometv.master.impl.dao.PlayListItemDao#findByFile(java.io.File)
     */
    @Override
    public PlayListItem findByFile(File file) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PlayListItem> query = criteriaBuilder.createQuery(getType());
        Root<PlayListItem> from = query.from(getType());
        query = query.where(criteriaBuilder.equal(from.get(PlayListItem_.file), file));
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
    protected Class<PlayListItem> getType() {
        return PlayListItem.class;
    }
}
