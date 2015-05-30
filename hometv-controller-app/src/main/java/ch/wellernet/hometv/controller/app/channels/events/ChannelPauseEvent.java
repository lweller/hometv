package ch.wellernet.hometv.controller.app.channels.events;

public class ChannelPauseEvent extends ChannelControlEvent {

    public ChannelPauseEvent(int channelId) {
        super(channelId);
    }
}
