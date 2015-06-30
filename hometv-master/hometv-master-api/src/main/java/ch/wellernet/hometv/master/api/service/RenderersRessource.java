/**
 *
 */
package ch.wellernet.hometv.master.api.service;

import org.restlet.resource.Get;
import org.restlet.resource.Put;

import ch.wellernet.hometv.common.api.model.RendererInfo;
import ch.wellernet.hometv.master.api.model.RendererList;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public interface RenderersRessource {

    @Get
    public RendererList load();

    @Put
    public void register(RendererInfo rendererInfo);
}
