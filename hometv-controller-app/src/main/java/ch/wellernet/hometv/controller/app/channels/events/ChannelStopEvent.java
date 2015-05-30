package ch.wellernet.hometv.controller.app.channels.events;

public class ChannelStopEvent extends ChannelControlEvent {

    public ChannelStopEvent(int channelId) {
        super(channelId);
    }
}
