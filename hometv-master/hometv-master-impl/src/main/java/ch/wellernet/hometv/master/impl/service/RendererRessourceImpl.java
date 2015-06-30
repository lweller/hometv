package ch.wellernet.hometv.master.impl.service;

import static java.lang.Integer.parseInt;

import javax.annotation.Resource;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ch.wellernet.hometv.master.api.model.Renderer;
import ch.wellernet.hometv.master.api.service.RendererResource;
import ch.wellernet.hometv.master.impl.dao.ChannelDao;
import ch.wellernet.hometv.master.impl.dao.RendererDao;
import ch.wellernet.hometv.master.impl.renderer.RendererManager;
import ch.wellernet.restlet.spring.Restlet;

@Restlet(router = "root", uriTemplate = "/channel/{id}")
public class RendererRessourceImpl extends ServerResource implements RendererResource {

    @Resource
    private RendererDao rendererDao;

    @Resource
    private ChannelDao channelDao;

    @Resource
    private RendererManager rendererManager;

    private int id;

    /**
     * @see ch.wellernet.hometv.master.api.service.RendererResource#connect(int)
     */
    @Override
    public void connect(int channedId) {
        rendererManager.connect(id, channedId);
    }

    /**
     * @see ch.wellernet.hometv.master.api.service.RendererResource#disconnect()
     */
    @Override
    public void disconnect() {
        rendererManager.disconnect(id);
    }

    /**
     * @see ch.wellernet.hometv.master.api.service.RendererResource#load()
     */
    @Override
    public Renderer load() {
        return rendererDao.find(id);
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
