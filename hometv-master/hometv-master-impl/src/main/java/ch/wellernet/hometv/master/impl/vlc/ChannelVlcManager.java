/**
 *
 */
package ch.wellernet.hometv.master.impl.vlc;

import static ch.wellernet.hometv.master.api.model.ChannelRestartMode.FROM_BEGIN_OF_LAST_ITEM;
import static ch.wellernet.hometv.master.api.model.ChannelRestartMode.FROM_BEGIN_OF_PLAY_LIST;
import static ch.wellernet.hometv.master.api.model.ChannelState.IDLE;
import static ch.wellernet.hometv.master.api.model.ChannelState.PLAYING;
import static ch.wellernet.hometv.master.api.model.ChannelState.STOPPED;
import static ch.wellernet.vlclib.MediaType.BROADCAST;
import static java.lang.String.format;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ch.wellernet.hometv.master.api.model.Channel;
import ch.wellernet.hometv.master.api.model.ChannelRestartMode;
import ch.wellernet.hometv.master.api.model.ChannelState;
import ch.wellernet.hometv.master.api.model.PlayList;
import ch.wellernet.hometv.master.api.model.PlayListItem;
import ch.wellernet.hometv.master.impl.dao.ChannelDao;
import ch.wellernet.hometv.master.impl.dao.PlayListItemDao;
import ch.wellernet.vlclib.VlcConnectionException;
import ch.wellernet.vlclib.VlcInput;
import ch.wellernet.vlclib.VlcManager;
import ch.wellernet.vlclib.VlcMedia;
import ch.wellernet.vlclib.VlcOption;
import ch.wellernet.vlclib.VlcOutput;

/**
 * Component that handles high level operations on VLC media player related to channels.
 *
 * @author Lucien Weller <lucien@wellernet.ch>
 * @since 1.0.0
 */
@Component
@Transactional(propagation = REQUIRED)
public class ChannelVlcManager {

    private static final Log LOG = LogFactory.getLog(ChannelVlcManager.class);

    @Autowired(required = false)
    private String pauseMeidaItemPath;

    @Resource
    private ChannelDao channelDao;

    @Resource
    private PlayListItemDao playListItemDao;

    @Resource
    private VlcManager vlcManager;

    /**
     * Creates a new media for a channel on VLC media player and configures its output .
     *
     * @returns the new channel
     * @throws ChannelVlcException
     *             when a unexpected problem with VLC media player occurs (see {@link ChannelVlcException})
     */
    public Channel createChannel() throws ChannelVlcException {
        Channel channel = new Channel.Builder().build(channelDao);
        String mediaName = buildMediaName(channel);
        try {
            vlcManager.createMedia(new VlcMedia(mediaName, BROADCAST, true, new VlcOutput.Builder().module("gather").module("std")
                    .property("access", "http").property("mux", "ps").property("dst", format(":8080/%s", mediaName)).build(), new VlcOption(
                            "sout-keep")));
        } catch (VlcConnectionException exception) {
            LOG.warn("Caught exception", exception);
            throw new ChannelVlcException("Cannot create new channel on Vlc media player.");
        }
        LOG.debug(format("Created channel ID  %d", channel.getId()));
        return channel;
    }

    public String getPauseMeidaItemPath() {
        return pauseMeidaItemPath;
    }

    /**
     * Loads the channel from data store.
     *
     * @param channelId
     *            the ID of channel that should be loaded
     * @return the coresponding channel if it exists, <code>null</code> otherwise
     */
    public Channel loadChannel(int channelId) {
        Channel channel = channelDao.find(channelId);
        if (channel == null) {
            LOG.debug(format("Channel ID  %d not found", channelId));
        } else {
            LOG.debug(format("Loaded channel ID  %d", channelId));
        }
        return channel;
    }

    /**
     * Stops playing the media corresponding to channel on VLC media player if it's currently playing.
     *
     * @param channelId
     *            the ID of channel that should stopped
     * @throws ChannelVlcException
     *             when a unexpected problem with VLC media player occurs (see {@link ChannelVlcException})
     */
    public void pause(int channelId) throws ChannelVlcException {
        Channel channel = loadChannel(channelId);
        if (channel == null) {
            LOG.debug("Did nothing as channel does not exist");
            return;
        }
        if (channel.getState() != PLAYING) {
            LOG.debug(format("Channel ID %d is currently not playing", channel.getId()));
        } else {
            try {
                String mediaName = buildMediaName(channel);
                vlcManager.toggleLoopState(mediaName);
                vlcManager.addInputItem(mediaName, new VlcInput(pauseMeidaItemPath));
                vlcManager.play(mediaName, channel.getPlayList().getItems().size() + 1); // input item list in VLC is 1 based
                for (int i = 0; i < channel.getPlayList().getItems().size(); i++) {
                    vlcManager.removeInputItem(mediaName, 1);
                }
            } catch (VlcConnectionException exception) {
                LOG.warn("Caught exception", exception);
                throw new ChannelVlcException(format("Failed to pause media fro channel %s.", channel.getId()));
            }
            channel.setState(ChannelState.PAUSED);
            LOG.debug(format("Channel ID %d paused", channel.getId()));
        }
    }

    /**
     * Starts playing current item on media corresponding to channel on VLC media player if it's not already playing.
     *
     * @param channelId
     *            the ID of channel that should start playing
     * @throws ChannelVlcException
     *             when a unexpected problem with VLC media player occurs (see {@link ChannelVlcException})
     */
    public void play(int channelId) throws ChannelVlcException {
        Channel channel = loadChannel(channelId);
        if (channel == null) {
            LOG.debug("Did nothing as channel does not exist");
            return;
        }
        PlayListItem currentPlayListItem = channel.getCurrentPlayListItem();
        long currentPosition = channel.getCurrentPosition().getMillis();
        int index = channel.getPlayList().getItems().indexOf(currentPlayListItem) + 1; // input item list in VLC is 1 based
        if (index > 0) {
            try {
                String mediaName = buildMediaName(channel);
                vlcManager.play(mediaName, index);
                if (channel.getCurrentPosition().getMillis() > 0) {
                    vlcManager.seek(mediaName, channel.getCurrentPosition());
                }
            } catch (VlcConnectionException exception) {
                LOG.warn("Caught exception", exception);
                throw new ChannelVlcException(format("Failed to start media for channel %d.", channel.getId()));
            }
            channel.setState(PLAYING);
            LOG.debug(format("Channel ID %d playing item %s from position %s ms", channel.getId(), currentPlayListItem.getTitle(), currentPosition));
        } else {
            LOG.warn("Seams that play list is inconsistent, as currently play item is not in play list");
        }
    }

    /**
     * @param channelId
     * @param mode
     */
    public void restart(int channelId, ChannelRestartMode mode) {
        Channel channel = loadChannel(channelId);
        if (channel == null) {
            LOG.debug("Did nothing as channel does not exist");
            return;
        }
        if (mode == FROM_BEGIN_OF_PLAY_LIST) {
            resetCurrentToBeginOfPlayList(channel);
        } else if (mode == FROM_BEGIN_OF_LAST_ITEM) {
            resetCurrentToBeginOfLastItem(channel);
        }
        play(channelId);
    }

    public void setPauseMeidaItemPath(String pauseMeidaItemPath) {
        this.pauseMeidaItemPath = pauseMeidaItemPath;
    }

    /**
     * @param channelId
     * @param playListItemIds
     */
    public void start(int channelId, List<Integer> playListItemIds) {
        Channel channel = loadChannel(channelId);
        if (channel == null) {
            LOG.debug("Did nothing as channel does not exist");
            return;
        }
        stop(channel.getId());
        updateVlcInput(channel, playListItemIds);
        play(channelId);
    }

    /**
     * Stops playing the media corresponding to channel on VLC media player if it's currently playing.
     *
     * @param channelId
     *            the ID of channel that should stopped
     * @throws ChannelVlcException
     *             when a unexpected problem with VLC media player occurs (see {@link ChannelVlcException})
     */
    public void stop(int channelId) throws ChannelVlcException {
        Channel channel = loadChannel(channelId);
        if (channel == null) {
            LOG.debug("Did nothing as channel does not exist");
            return;
        }
        if (channel.getState() == STOPPED || channel.getState() == IDLE) {
            LOG.debug(format("Channel ID %d is already stopped", channel.getId()));
        } else {
            try {
                vlcManager.stop(buildMediaName(channel));
            } catch (VlcConnectionException exception) {
                LOG.warn("Caught exception", exception);
                throw new ChannelVlcException(format("Failed to stop media fro channel %s.", channel.getId()));
            }
            channel.setState(STOPPED);
            LOG.debug(format("Channel ID %d stopped", channel.getId()));
        }
    }

    /**
     * @param channel
     */
    void resetCurrentToBeginOfLastItem(Channel channel) {
        stop(channel.getId());
        channel.setCurrentPosition(new Duration(0));
        LOG.debug(format("Current item for for channel ID %d set to %s at start position", channel.getId(), channel.getCurrentPlayListItem()
                .getTitle()));
    }

    /**
     * @param channel
     */
    void resetCurrentToBeginOfPlayList(Channel channel) {
        stop(channel.getId());
        PlayListItem currentPlayListItem = channel.getPlayList().getItems().get(0);
        channel.setCurrentPlayListItem(currentPlayListItem);
        channel.setCurrentPosition(new Duration(0));
        LOG.debug(format("Current item for for channel ID %d set to %s at start position", channel.getId(), currentPlayListItem.getTitle()));
    }

    /**
     * Updates the input of the media corresponding to the channel with currently defined play list items. First of all if media is currently playing,
     * it will be stopped. Then the whole input queue will be cleared and updated with new input items.
     *
     * @param channel
     *            channel which input should be updated.
     * @param playListItemIds
     *            list of ID's of play items in play list
     * @throws ChannelVlcException
     *             when a unexpected problem with VLC media player occurs (see {@link ChannelVlcException})
     */
    void updateVlcInput(Channel channel, List<Integer> playListItemIds) throws ChannelVlcException {
        String mediaName = buildMediaName(channel);
        PlayList playList = channel.getPlayList();
        try {
            vlcManager.clearInput(mediaName);
        } catch (VlcConnectionException exception) {
            LOG.warn("Caught exception", exception);
            throw new ChannelVlcException(format("Failed to clear input for channel %d on VLC media player.", channel.getId()));
        }
        playList.clear();
        LOG.debug(format("Play list of channel ID %d cleared", channel.getId()));

        if (playListItemIds.isEmpty()) {
            LOG.debug(format("Play list of channel ID %d left empty because no there are items to play", channel.getId()));
        } else {
            for (int id : playListItemIds) {
                PlayListItem item = playListItemDao.find(id);
                try {
                    vlcManager.addInputItem(mediaName, new VlcInput(item.getFile().getAbsolutePath()));
                } catch (VlcConnectionException exception) {
                    LOG.warn("Caught exception", exception);
                    throw new ChannelVlcException(
                            format("Failed to add item %s for channel %d on VLC media player", item.getTitle(), channel.getId()));
                }
                playList.addItem(item);
            }
            LOG.debug(format("Play list of channel ID %d populated", channel.getId()));
            resetCurrentToBeginOfPlayList(channel);
        }
    }

    private String buildMediaName(Channel channel) {
        return "channel" + channel.getId();
    }
}
