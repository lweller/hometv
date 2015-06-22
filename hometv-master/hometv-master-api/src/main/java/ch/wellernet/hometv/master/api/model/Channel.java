package ch.wellernet.hometv.master.api.model;

import static ch.wellernet.hometv.master.api.model.ChannelState.IDLE;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.EnumType.STRING;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.joda.time.Duration;

import ch.wellernet.hometv.util.model.IdentifyableObject;
import ch.wellernet.hometv.util.model.ModelObjectBuilder;

@Entity
@Table(name = "CHANNEL", schema = "HOMETV")
@SequenceGenerator(name = "primary_key", schema = "HOMETV", sequenceName = "SEQ_CHANNEL")
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

    @Embedded
    private PlayList playList;

    @Column(name = "STATE")
    @Enumerated(STRING)
    private ChannelState state;

    @ManyToOne(cascade = DETACH)
    @JoinColumn(name = "PLAY_LIST_ITEM_ID")
    private PlayListItem currentPlayListItem;

    @Column(name = "CURRENT_POSITION")
    private Duration currentPosition;

    @Column(name = "LAST_SYNCHRONISATION_WITH_MEDIA_PLAYER")
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
