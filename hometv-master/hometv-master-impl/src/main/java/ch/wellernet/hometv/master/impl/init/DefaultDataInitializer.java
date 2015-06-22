/**
 *
 */
package ch.wellernet.hometv.master.impl.init;

import static ch.wellernet.hometv.master.impl.model.Setting.INITIALIZED;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import ch.wellernet.hometv.master.impl.dao.SettingDao;
import ch.wellernet.hometv.master.impl.media.MediaItemManager;
import ch.wellernet.hometv.master.impl.model.Setting;
import ch.wellernet.hometv.master.impl.vlc.ChannelVlcManager;

/**
 * Initializes the database and VLC media player. If it's the first time it starts up or there is no persistent state, default properties will be
 * used. Otherwise persisted state will be restored.
 *
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Component
public class DefaultDataInitializer implements DataInitializer {

    @Resource
    private DefaultDataProperties properties;

    @Resource
    private SettingDao settingDao;

    @Resource
    private ChannelVlcManager channelVlcManager;

    @Resource
    private MediaItemManager mediaItemManager;

    /**
     * @see ch.wellernet.hometv.master.impl.init.DataInitializer#initData()
     */
    @Override
    public void initData() {
        Setting initializedSetting = settingDao.findByName(INITIALIZED);
        if (initializedSetting == null || !initializedSetting.getValueAsBoolean()) {
            for (int i = 0; i < properties.getChannelCount(); i++) {
                channelVlcManager.createChannel();
            }

            mediaItemManager.scanDirectory(properties.getMediaDirectory(), true);

            if (initializedSetting == null) {
                initializedSetting = new Setting(INITIALIZED);
                settingDao.attach(initializedSetting);
            }
            initializedSetting.setValue(true);
        }
    }
}
