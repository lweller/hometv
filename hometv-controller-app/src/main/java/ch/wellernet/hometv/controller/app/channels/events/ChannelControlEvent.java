package ch.wellernet.hometv.controller.app.channels.events;

public class ChannelControlEvent {
    private final int channelId;

    public ChannelControlEvent(int channelId) {
        this.channelId = channelId;
    }

    public int getChannelId() {
        return channelId;
    }
}
