package ch.wellernet.hometv.master.impl.service;

import static ch.wellernet.hometv.master.api.model.ChannelRestartMode.FROM_BEGIN_OF_PLAY_LIST;
import static ch.wellernet.hometv.master.api.model.ChannelState.PAUSED;
import static ch.wellernet.hometv.master.api.model.ChannelState.PLAYING;
import static ch.wellernet.hometv.master.api.model.ChannelState.STOPPED;
import static java.lang.Integer.parseInt;

import java.util.List;

import javax.annotation.Resource;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ch.wellernet.hometv.master.api.model.Channel;
import ch.wellernet.hometv.master.api.model.ChannelRestartMode;
import ch.wellernet.hometv.master.api.model.PlayList;
import ch.wellernet.hometv.master.api.service.ChannelRessource;
import ch.wellernet.hometv.master.impl.dao.ChannelDao;
import ch.wellernet.hometv.master.impl.dao.PlayListItemDao;
import ch.wellernet.hometv.util.restlet.Restlet;

@Restlet(router = "root", uriTemplate = "/channels/{id}")
public class ChannelRessourceImpl extends ServerResource implements ChannelRessource {

    @Resource
    private ChannelDao channelDao;

    @Resource
    private PlayListItemDao playListItemDao;

    private int id;

    /**
     * @see org.restlet.resource.ServerResource#get()
     */
    @Override
    public Channel load() {
        return channelDao.find(id);
    }

    /**
     * @see ch.wellernet.hometv.master.api.service.ChannelRessource#pause()
     */
    @Override
    public void pause() {
        Channel channel = load();
        if (channel != null) {
            if (channel.getState() == PLAYING) {
                channel.setState(PAUSED);
            }
        }
    }

    /**
     * @see ch.wellernet.hometv.master.api.service.ChannelRessource#restart(ch.wellernet.hometv.master.api.model.ChannelRestartMode)
     */
    @Override
    public void restart(ChannelRestartMode mode) {
        Channel channel = load();
        if (channel != null) {
            if (channel.getState() == STOPPED) {
                channel.setState(PLAYING);
                switch (mode) {
                case FROM_BEGIN_OF_PLAY_LIST:
                    channel.setCurrentPlayListItem(channel.getPlayList().getItems().get(0));
                    break;
                default:
                }
            }
        }
    }

    /**
     * @see ch.wellernet.hometv.master.api.service.ChannelRessource#resume()
     */
    @Override
    public void resume() {
        Channel channel = load();
        if (channel != null) {
            if (channel.getState() == PAUSED) {
                channel.setState(PLAYING);
            }
        }
    }

    /**
     * @see ch.wellernet.hometv.master.api.service.ChannelRessource#start(java.util.List)
     */
    @Override
    public void start(List<Integer> playListItemIds) {
        stop();
        Channel channel = load();
        if (channel != null) {
            PlayList playList = channel.getPlayList();
            playList.clear();
            for (int id : playListItemIds) {
                playList.addItem(playListItemDao.find(id));
            }
            channel.setCurrentPlayListItem(playList.getItems().isEmpty() ? null : playList.getItems().get(0));
            channel.setState(STOPPED);
            restart(FROM_BEGIN_OF_PLAY_LIST);
        }
    }

    /**
     * @see ch.wellernet.hometv.master.api.service.ChannelRessource#stop()
     */
    @Override
    public void stop() {
        Channel channel = load();
        if (channel != null) {
            if (channel.getState() == PLAYING || channel.getState() == PAUSED) {
                channel.setState(STOPPED);
            }
        }
    }

    /**
     * @see org.restlet.resource.Resource#doInit()
     */
    @Override
    protected void doInit() throws ResourceException {
        super.doInit();
        id = parseInt((String) getRequestAttributes().get("id"));
    }

    void setId(int id) {
        this.id = id;
    }

}
