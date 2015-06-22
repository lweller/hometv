package ch.wellernet.hometv.master.impl.init;

import static ch.wellernet.hometv.master.impl.init.DefaultDataProperties.DEFAULT_MEDIA_DIRECTORY;
import static ch.wellernet.hometv.master.impl.init.DefaultDataProperties.DEFUALT_CHANNEL_COUNT;
import static ch.wellernet.hometv.master.impl.model.Setting.INITIALIZED;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import ch.wellernet.hometv.master.impl.dao.SettingDao;
import ch.wellernet.hometv.master.impl.media.MediaItemManager;
import ch.wellernet.hometv.master.impl.model.Setting;
import ch.wellernet.hometv.master.impl.vlc.ChannelVlcManager;

public class DefaultDataInitializerTest {

    // under test
    @InjectMocks
    private DefaultDataInitializer dataInitializer;

    @Spy
    private DefaultDataProperties properties;

    @Mock
    private ChannelVlcManager channelVlcManager;

    @Mock
    private MediaItemManager mediaItemManager;

    @Mock
    private SettingDao settingDao;

    @Before
    public void setup() {
        properties = new DefaultDataProperties();

        dataInitializer = new DefaultDataInitializer();
        initMocks(this);
    }

    @Test
    public void shouldInitDataIfInitSettingIsPresentButFalse() {
        // given
        Setting setting = new Setting(INITIALIZED);
        setting.setValue(false);
        doReturn(setting).when(settingDao).findByName(INITIALIZED);

        // when
        dataInitializer.initData();

        // then
        verify(channelVlcManager, times(DEFUALT_CHANNEL_COUNT)).createChannel();
        verify(mediaItemManager).scanDirectory(new File(DEFAULT_MEDIA_DIRECTORY), true);
    }

    @Test
    public void shouldInitDataWithCustomProperties() {
        // given
        final int channelCount = 42;
        properties.setChannelCount(channelCount);
        final File directory = new File("/path/to/films");
        properties.setMediaDirectory(directory);

        // when
        dataInitializer.initData();

        // then
        verify(channelVlcManager, times(channelCount)).createChannel();
        verify(mediaItemManager).scanDirectory(directory, true);
    }

    @Test
    public void shouldInitDataWithDefaultProperties() {
        // given
        // only default properties

        // when
        dataInitializer.initData();

        // then
        verify(channelVlcManager, times(DEFUALT_CHANNEL_COUNT)).createChannel();
        verify(mediaItemManager).scanDirectory(new File(DEFAULT_MEDIA_DIRECTORY), true);
    }

    @Test
    public void shouldNotInitializeIdAlreadyInitialized() {
        // given
        Setting setting = new Setting(INITIALIZED);
        setting.setValue(true);
        doReturn(setting).when(settingDao).findByName(INITIALIZED);

        // when
        dataInitializer.initData();

        // then
        verifyZeroInteractions(channelVlcManager, mediaItemManager);
    }
}
