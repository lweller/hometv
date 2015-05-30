package ch.wellernet.hometv.controller.app.channels;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import roboguice.RoboGuice;
import roboguice.event.EventManager;
import roboguice.event.Observes;
import android.content.Context;
import ch.wellernet.hometv.controller.app.channels.events.ChannelChangedEvent;
import ch.wellernet.hometv.controller.app.channels.events.ChannelPauseEvent;
import ch.wellernet.hometv.controller.app.channels.events.ChannelRestartEvent;
import ch.wellernet.hometv.controller.app.channels.events.ChannelResumeEvent;
import ch.wellernet.hometv.controller.app.channels.events.ChannelStartEvent;
import ch.wellernet.hometv.controller.app.channels.events.ChannelStopEvent;
import ch.wellernet.hometv.master.api.service.ChannelRessource;
import ch.wellernet.hometv.util.restlet.ClientRessourceFactory;

import com.google.inject.Inject;

@EBean
public class ChannelController {

    public static class Provider implements com.google.inject.Provider<ChannelController> {

        @Inject
        private Context context;

        @Override
        public ChannelController get() {
            ChannelController controller = ChannelController_.getInstance_(null);
            RoboGuice.getInjector(context).injectMembersWithoutViews(controller);
            return controller;
        }
    }

    @Inject
    private EventManager eventManager;

    @Inject
    private ClientRessourceFactory<Integer, ChannelRessource> channelRessourceFactory;

    @Background
    public void doPause(@Observes ChannelPauseEvent event) {
        channelRessourceFactory.getRessource(event.getChannelId()).pause();
        reload(event.getChannelId());
    }

    @Background
    public void doRestart(@Observes ChannelRestartEvent event) {
        channelRessourceFactory.getRessource(event.getChannelId()).restart(event.getRestartMode());
        reload(event.getChannelId());
    }

    @Background
    public void doResume(@Observes ChannelResumeEvent event) {
        channelRessourceFactory.getRessource(event.getChannelId()).resume();
        reload(event.getChannelId());
    }

    @Background
    public void doStart(@Observes ChannelStartEvent event) {
        List<Integer> playListItemIds = new ArrayList<Integer>();
        for (int id : event.getPlayListItemsIds()) {
            playListItemIds.add(id);
        }
        channelRessourceFactory.getRessource(event.getChannelId()).start(playListItemIds);
        reload(event.getChannelId());
    }

    @Background
    public void doStop(@Observes ChannelStopEvent event) {
        channelRessourceFactory.getRessource(event.getChannelId()).stop();
        reload(event.getChannelId());
    }

    @Background
    public void reload(int channelId) {
        eventManager.fire(new ChannelChangedEvent(channelRessourceFactory.getRessource(channelId).load()));
    }
}
