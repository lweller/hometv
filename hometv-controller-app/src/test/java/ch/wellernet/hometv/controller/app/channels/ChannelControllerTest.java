package ch.wellernet.hometv.controller.app.channels;

import static ch.wellernet.hometv.master.api.model.ChannelRestartMode.FROM_BEGIN_OF_PLAY_LIST;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.ArrayUtils.toObject;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static roboguice.RoboGuice.injectMembers;
import static roboguice.RoboGuice.overrideApplicationInjector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Spy;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import roboguice.RoboGuice;
import roboguice.event.EventManager;
import android.os.Build;
import ch.wellernet.hometv.controller.app.channels.events.ChannelChangedEvent;
import ch.wellernet.hometv.controller.app.channels.events.ChannelPauseEvent;
import ch.wellernet.hometv.controller.app.channels.events.ChannelRestartEvent;
import ch.wellernet.hometv.controller.app.channels.events.ChannelResumeEvent;
import ch.wellernet.hometv.controller.app.channels.events.ChannelStartEvent;
import ch.wellernet.hometv.controller.app.channels.events.ChannelStopEvent;
import ch.wellernet.hometv.master.api.model.Channel;
import ch.wellernet.hometv.master.api.service.ChannelRessource;
import ch.wellernet.hometv.test.model.PredefinedIdInitializer;
import ch.wellernet.restlet.util.ClientRessourceFactory;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = Build.VERSION_CODES.LOLLIPOP)
public class ChannelControllerTest {

    private static final int CHANNEL_ID = 42;

    // under test
    @Spy
    private ChannelController channelController;

    private ChannelAdministrationActivity activity;

    @Mock
    private ClientRessourceFactory<Integer, ChannelRessource> channelRessourceFactory;

    @Mock
    private EventManager eventManager;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(ChannelAdministrationActivity_.class);

        initMocks(this);
        overrideApplicationInjector(activity.getApplication(), new AbstractModule() {

            @Override
            protected void configure() {
                binder.bind(EventManager.class).toInstance(eventManager);
                binder.bind(new TypeLiteral<ClientRessourceFactory<Integer, ChannelRessource>>() {
                }).toInstance(channelRessourceFactory);
            }
        });

        injectMembers(activity.getApplication(), channelController);
    }

    @Test
    public void shouldCallPauseAndReloadFromChannelRessource() {
        // given
        ChannelRessource channelRessource = mock(ChannelRessource.class);
        when(channelRessourceFactory.getRessource(CHANNEL_ID)).thenReturn(channelRessource);
        doNothing().when(channelController).reload(anyInt());

        // when
        channelController.doPause(new ChannelPauseEvent(CHANNEL_ID));

        // then
        InOrder order = inOrder(channelController, channelRessource);
        order.verify(channelRessource).pause();
        order.verify(channelController).reload(CHANNEL_ID);
    }

    @Test
    public void shouldCallRestartAndReloadFromChannelRessource() {
        // given
        ChannelRessource channelRessource = mock(ChannelRessource.class);
        when(channelRessourceFactory.getRessource(CHANNEL_ID)).thenReturn(channelRessource);
        doNothing().when(channelController).reload(anyInt());

        // when
        channelController.doRestart(new ChannelRestartEvent(CHANNEL_ID, FROM_BEGIN_OF_PLAY_LIST));

        // then
        InOrder order = inOrder(channelController, channelRessource);
        order.verify(channelRessource).restart(FROM_BEGIN_OF_PLAY_LIST);
        order.verify(channelController).reload(CHANNEL_ID);
    }

    @Test
    public void shouldCallResumeAndReloadFromChannelRessource() {
        // given
        ChannelRessource channelRessource = mock(ChannelRessource.class);
        when(channelRessourceFactory.getRessource(CHANNEL_ID)).thenReturn(channelRessource);
        doNothing().when(channelController).reload(anyInt());

        // when
        channelController.doResume(new ChannelResumeEvent(CHANNEL_ID));

        // then
        InOrder order = inOrder(channelController, channelRessource);
        order.verify(channelRessource).resume();
        order.verify(channelController).reload(CHANNEL_ID);
    }

    @Test
    public void shouldCallStartAndReloadFromChannelRessource() {
        // given
        ChannelRessource channelRessource = mock(ChannelRessource.class);
        when(channelRessourceFactory.getRessource(CHANNEL_ID)).thenReturn(channelRessource);
        doNothing().when(channelController).reload(anyInt());
        int[] playListImtemIds = { 101, 102, 142 };

        // when
        channelController.doStart(new ChannelStartEvent(CHANNEL_ID, playListImtemIds));

        // then
        InOrder order = inOrder(channelController, channelRessource);
        order.verify(channelRessource).start(asList(toObject(playListImtemIds)));
        order.verify(channelController).reload(CHANNEL_ID);
    }

    @Test
    public void shouldCallStopAndReloadFromChannelRessource() {
        // given
        ChannelRessource channelRessource = mock(ChannelRessource.class);
        when(channelRessourceFactory.getRessource(CHANNEL_ID)).thenReturn(channelRessource);
        doNothing().when(channelController).reload(anyInt());

        // when
        channelController.doStop(new ChannelStopEvent(CHANNEL_ID));

        // then
        InOrder order = inOrder(channelController, channelRessource);
        order.verify(channelRessource).stop();
        order.verify(channelController).reload(CHANNEL_ID);
    }

    @Test
    public void shouldLoadFromChannelRessourceAndFireUpdateEvent() {
        // given
        ChannelRessource channelRessource = mock(ChannelRessource.class);
        when(channelRessourceFactory.getRessource(CHANNEL_ID)).thenReturn(channelRessource);
        Channel channel = new Channel.Builder().build(new PredefinedIdInitializer<Channel>(CHANNEL_ID));
        when(channelRessource.load()).thenReturn(channel);

        // when
        channelController.reload(CHANNEL_ID);

        // then
        InOrder order = inOrder(channelRessource, eventManager);
        order.verify(channelRessource).load();
        order.verify(eventManager).fire(new ChannelChangedEvent(channel));
    }

    @After
    public void teardown() {
        RoboGuice.Util.reset();
    }
}
