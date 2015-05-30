package ch.wellernet.hometv.master.api.model;

import static ch.wellernet.hometv.master.api.model.ChannelState.IDLE;
import ch.wellernet.hometv.util.model.IdentifyableObject;

public class Channel extends IdentifyableObject<Integer> {

    private static final long serialVersionUID = 1L;

    private PlayList playList;
    private PlayListItem currentPlayListItem;
    private ChannelState state;

    public Channel(int id) {
        super(id);
        this.playList = new PlayList();
        this.state = IDLE;
    }

    private Channel() {
        super(null);
    }

    public PlayListItem getCurrentPlayListItem() {
        return currentPlayListItem;
    }

    public PlayList getPlayList() {
        return playList;
    }

    public ChannelState getState() {
        return state;
    }

    public void setCurrentPlayListItem(PlayListItem currentPlayListItem) {
        this.currentPlayListItem = currentPlayListItem;
    }

    public void setState(ChannelState state) {
        this.state = state;
    }
}
