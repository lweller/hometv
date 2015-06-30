/**
 *
 */
package ch.wellernet.hometv.master.impl.dao;

import ch.wellernet.hibernate.dao.GenericDao;
import ch.wellernet.hometv.master.api.model.Renderer;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public interface RendererDao extends GenericDao<Integer, Renderer> {
    /**
     * Retrieves a renderer by its device ID.
     *
     * @param deviceId
     *            the device ID of hardware device.
     * @return the corresponding renderer if it exists or <code>null</code> if not.
     */
    public Renderer findByInfo(String deviceId);
}
