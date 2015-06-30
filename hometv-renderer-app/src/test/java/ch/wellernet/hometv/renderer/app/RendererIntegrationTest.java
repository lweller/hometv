/**
 *
 */
package ch.wellernet.hometv.renderer.app;

import static org.restlet.data.Protocol.HTTP;

import org.junit.Test;
import org.restlet.Client;
import org.restlet.resource.ClientResource;

import ch.wellernet.hometv.renderer.api.service.RemoteControlResource;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public class RendererIntegrationTest {

    @Test
    public void test() {
        Client client = new Client(HTTP);
        ClientResource root = new ClientResource("http://localhost:8200/");
        root.setNext(client);
        root.setEntityBuffering(true);
        RemoteControlResource remoteControl = root.getChild(".", RemoteControlResource.class);
        remoteControl.play("http://localhost:8080/channel50");
    }
}
