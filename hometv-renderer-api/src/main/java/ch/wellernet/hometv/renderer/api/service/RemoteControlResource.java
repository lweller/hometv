/**
 *
 */
package ch.wellernet.hometv.renderer.api.service;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

import ch.wellernet.hometv.renderer.api.model.RendererInfo;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public interface RemoteControlResource {
    @Get
    public RendererInfo getInfos();

    @Get("?ping")
    public void ping();

    @Post("?play")
    public void play(String url);
}
