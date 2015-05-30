package ch.wellernet.hometv.controller.app.channels.events;

public class ChannelStartEvent extends ChannelControlEvent {

    private final int[] playListItemsIds;

    public ChannelStartEvent(int channelId, int... playListItemsIds) {
        super(channelId);
        this.playListItemsIds = playListItemsIds;
    }

    public int[] getPlayListItemsIds() {
        return playListItemsIds;
    }
}
