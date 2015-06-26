/**
 *
 */
package ch.wellernet.hometv.master.impl.dao;

import ch.wellernet.hibernate.dao.GenericDao;
import ch.wellernet.hometv.master.impl.model.Setting;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public interface SettingDao extends GenericDao<Integer, Setting> {
    /**
     * Retrieves a setting with its name.
     *
     * @param name
     *            the name of setting
     * @return the corresponding setting if it exists or <code>null</code> if not.
     */
    public Setting findByName(String name);
}
