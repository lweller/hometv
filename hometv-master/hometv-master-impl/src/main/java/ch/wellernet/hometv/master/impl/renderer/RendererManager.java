/**
 *
 */
package ch.wellernet.hometv.master.impl.renderer;

import static ch.wellernet.hometv.master.api.model.ChannelState.PLAYING;
import static java.lang.String.format;
import static org.restlet.data.Protocol.HTTP;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Client;
import org.restlet.resource.ClientResource;
import org.springframework.stereotype.Component;

import ch.wellernet.hometv.common.api.model.RendererInfo;
import ch.wellernet.hometv.master.api.model.Channel;
import ch.wellernet.hometv.master.api.model.Renderer;
import ch.wellernet.hometv.master.impl.dao.RendererDao;
import ch.wellernet.hometv.master.impl.vlc.ChannelVlcManager;
import ch.wellernet.hometv.renderer.api.service.RemoteControlResource;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Component
public class RendererManager {

    private static final Log LOG = LogFactory.getLog(RendererManager.class);

    @Resource
    RendererDao rendererDao;

    @Resource
    private ChannelVlcManager channelVlcManager;

    /**
     * Connects a renderer to a given channel. If channel is currently playing a coresponding command is send to renderer after successful connection.
     *
     * @param rendererId
     *            the ID of renderer to connect
     * @param channelId
     *            the ID of channel to connect to
     */
    public void connect(int rendererId, int channelId) {
        LOG.trace(format("Trying to connect renderer %s to channel %s", rendererId, channelId));
        Renderer renderer = loadRenderer(rendererId);
        if (renderer != null) {
            Channel channel = channelVlcManager.loadChannel(channelId);
            if (channel != null) {
                renderer.setChannel(channel);
                if (channel.getState() == PLAYING) {
                    connectRendererRemoteControl(renderer.getInfo()).play(channelVlcManager.buildStreamUrl(channelId));
                }
            }
        }
    }

    /**
     * @param rendererId
     */
    public void disconnect(int rendererId) {
        LOG.trace(format("Trying to disconnect renderer %s", rendererId));
        Renderer renderer = loadRenderer(rendererId);
        if (renderer != null && renderer.getChannel() != null) {
            renderer.setChannel(null);
            connectRendererRemoteControl(renderer.getInfo()).stop();
        }
    }

    /**
     * Loads the renderer from data store.
     *
     * @param rendererId
     *            the ID of renderer that should be loaded
     * @return the corresponding renderer if it exists, <code>null</code> otherwise
     */
    public Renderer loadRenderer(int rendererId) {
        Renderer renderer = rendererDao.find(rendererId);
        if (renderer == null) {
            LOG.debug(format("Renderer ID  %d not found", rendererId));
        } else {
            LOG.debug(format("Loaded renderer ID  %d", rendererId));
        }
        return renderer;
    }

    private RemoteControlResource connectRendererRemoteControl(RendererInfo rendererInfo) {
        Client client = new Client(HTTP);
        ClientResource root = new ClientResource(format("http://%s:8100/", rendererInfo.getHostname()));
        root.setNext(client);
        root.setEntityBuffering(true);
        return root.get(RemoteControlResource.class);
    }
}
