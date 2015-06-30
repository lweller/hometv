/**
 *
 */
package ch.wellernet.hometv.master.impl.service;

import javax.annotation.Resource;

import org.restlet.resource.ServerResource;

import ch.wellernet.hometv.common.api.model.RendererInfo;
import ch.wellernet.hometv.master.api.model.Renderer;
import ch.wellernet.hometv.master.api.model.RendererList;
import ch.wellernet.hometv.master.api.service.RenderersRessource;
import ch.wellernet.hometv.master.impl.dao.RendererDao;
import ch.wellernet.restlet.spring.Restlet;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Restlet(router = "root", uriTemplate = "/renderers")
public class RenderersResource extends ServerResource implements RenderersRessource {

    @Resource
    private RendererDao rendererDao;

    /**
     * @see ch.wellernet.hometv.master.api.service.RenderersRessource#load()
     */
    @Override
    public RendererList load() {
        return new RendererList(rendererDao.findAll());
    }

    /**
     * @see ch.wellernet.hometv.master.api.service.RenderersRessource#register(ch.wellernet.hometv.common.api.model.RendererInfo)
     */
    @Override
    public void register(RendererInfo rendererInfo) {
        new Renderer.Builder(rendererInfo).build(rendererDao);
    }
}
