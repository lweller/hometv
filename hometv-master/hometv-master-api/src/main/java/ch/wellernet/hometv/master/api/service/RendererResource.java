/**
 *
 */
package ch.wellernet.hometv.master.api.service;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

import ch.wellernet.hometv.master.api.model.Renderer;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public interface RendererResource {
    @Post("?connect")
    public void connect(int channedId);

    @Post("?disconnect")
    public void disconnect();

    @Get
    public Renderer load();
}
