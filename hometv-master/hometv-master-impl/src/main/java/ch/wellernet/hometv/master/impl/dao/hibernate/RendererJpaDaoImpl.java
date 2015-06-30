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
import ch.wellernet.hometv.common.api.model.RendererInfo_;
import ch.wellernet.hometv.master.api.model.Renderer;
import ch.wellernet.hometv.master.api.model.Renderer_;
import ch.wellernet.hometv.master.impl.dao.RendererDao;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Repository
public class RendererJpaDaoImpl extends AbstractJpaDaoImpl<Integer, Renderer> implements RendererDao {

    @Override
    public Renderer findByInfo(String deviceId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Renderer> query = criteriaBuilder.createQuery(getType());
        Root<Renderer> renderer = query.from(getType());
        query = query.where(criteriaBuilder.equal(renderer.get(Renderer_.info).get(RendererInfo_.deviceId), deviceId));
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
    protected Class<Renderer> getType() {
        return Renderer.class;
    }
}
