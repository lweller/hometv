package ch.wellernet.hometv.master.api.model;

import static ch.wellernet.hometv.master.api.model.ChannelState.IDLE;

import java.util.Date;

import org.joda.time.Duration;

import ch.wellernet.hometv.util.model.IdentifyableObject;
import ch.wellernet.hometv.util.model.ModelObjectBuilder;

public class Channel extends IdentifyableObject<Integer> {

    public static class Builder extends ModelObjectBuilder<Integer, Channel> {
        private Channel instance;

        public Builder() {
            instance = new Channel();
        }

        @Override
        protected Channel build() {
            return instance;
        }
    }

    private static final long serialVersionUID = 1L;

    private PlayList playList;
    private ChannelState state;
    private PlayListItem currentPlayListItem;
    private Duration currentPosition;
    private Date lastSynchronisaionWithMediaPlayer;

    private Channel() {
        this.playList = new PlayList();
        this.state = IDLE;
    }

    public PlayListItem getCurrentPlayListItem() {
        return currentPlayListItem;
    }

    public Duration getCurrentPosition() {
        return currentPosition;
    }

    public Date getLastSynchronisaionWithMediaPlayer() {
        return lastSynchronisaionWithMediaPlayer;
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

    public void setCurrentPosition(Duration currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setLastSynchronisaionWithMediaPlayer(Date lastSynchronisaionWithMediaPlayer) {
        this.lastSynchronisaionWithMediaPlayer = lastSynchronisaionWithMediaPlayer;
    }

    public void setState(ChannelState state) {
        this.state = state;
    }
}
