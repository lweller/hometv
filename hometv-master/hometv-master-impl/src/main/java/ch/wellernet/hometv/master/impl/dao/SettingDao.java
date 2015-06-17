/**
 *
 */
package ch.wellernet.hometv.master.impl.dao;

import model.Setting;
import ch.wellernet.hometv.util.dao.GenericDao;

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
