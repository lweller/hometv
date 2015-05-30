package ch.wellernet.hometv.controller.app.channels.events;

import ch.wellernet.hometv.master.api.model.ChannelRestartMode;

public class ChannelRestartEvent extends ChannelControlEvent {

    private final ChannelRestartMode restartMode;

    public ChannelRestartEvent(int channelId, ChannelRestartMode restartMode) {
        super(channelId);
        this.restartMode = restartMode;
    }

    public ChannelRestartMode getRestartMode() {
        return restartMode;
    }
}
