package ch.wellernet.hometv.master.impl.dao.inmemory;

import model.Setting;

import org.springframework.stereotype.Component;

import ch.wellernet.hometv.master.impl.dao.SettingDao;
import ch.wellernet.hometv.util.dao.BaseInMemoryGenericDao;
import ch.wellernet.hometv.util.dao.IntegerIdGenerator;

@Component
public class SettingsInMemoryDaoImpl extends BaseInMemoryGenericDao<Integer, Setting> implements SettingDao {

    public SettingsInMemoryDaoImpl() {
        super(new IntegerIdGenerator<Setting>());
    }

    /**
     * @see ch.wellernet.hometv.master.impl.dao.SettingDao#findByName(java.lang.String)
     */
    @Override
    public Setting findByName(String name) {
        if (name == null) {
            return null;
        }
        for (Setting setting : findAll()) {
            if (setting.getName().equals(name)) {
                return setting;
            }
        }
        return null;
    }
}
