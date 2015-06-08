package ch.wellernet.hometv.master.impl.service;

import static ch.wellernet.hometv.master.api.model.ChannelState.PAUSED;
import static ch.wellernet.hometv.master.api.model.ChannelState.PLAYING;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ch.wellernet.hometv.master.api.model.Channel;
import ch.wellernet.hometv.master.api.model.ChannelRestartMode;
import ch.wellernet.hometv.master.api.service.ChannelRessource;
import ch.wellernet.hometv.master.impl.vlc.ChannelVlcManager;
import ch.wellernet.hometv.util.restlet.Restlet;

@Restlet(router = "root", uriTemplate = "/channels/{id}")
public class ChannelRessourceImpl extends ServerResource implements ChannelRessource {

    private static final Log LOG = LogFactory.getLog(ChannelRessourceImpl.class);

    @Resource
    private ChannelVlcManager channelVlcManager;

    private int id;

    /**
     * @see org.restlet.resource.ServerResource#get()
     */
    @Override
    public Channel load() {
        return channelVlcManager.loadChannel(id);
    }

    /**
     * @see ch.wellernet.hometv.master.api.service.ChannelRessource#pause()
     */
    @Override
    public void pause() {
        LOG.trace(format("Trying to pause channel %s", id));
        Channel channel = load();
        if (channel != null) {
            if (channel.getState() == PLAYING) {
                LOG.debug(format("Pausing channel %d", id));
                channel.setState(PAUSED);
            } else {
                LOG.debug(format("Channel %d already paused, so there's nothing to to", id));
            }
        }
    }

    /**
     * @see ch.wellernet.hometv.master.api.service.ChannelRessource#restart(ch.wellernet.hometv.master.api.model.ChannelRestartMode)
     */
    @Override
    public void restart(ChannelRestartMode mode) {
        channelVlcManager.restart(id, mode);
    }

    /**
     * @see ch.wellernet.hometv.master.api.service.ChannelRessource#resume()
     */
    @Override
    public void resume() {
        LOG.trace(format("Trying to resume channel %s", id));
        Channel channel = load();
        if (channel != null) {
            if (channel.getState() == PAUSED) {
                LOG.debug(format("Continues playing channel %d after pause", id));
                channel.setState(PLAYING);
            } else {
                LOG.debug(format("Channel %d is curretnly not paused, so there's nothing to do", id));
            }
        }
    }

    /**
     * @see ch.wellernet.hometv.master.api.service.ChannelRessource#start(java.util.List)
     */
    @Override
    public void start(List<Integer> playListItemIds) {
        channelVlcManager.start(id, playListItemIds);
    }

    /**
     * @see ch.wellernet.hometv.master.api.service.ChannelRessource#stop()
     */
    @Override
    public void stop() {
        channelVlcManager.stop(id);
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
