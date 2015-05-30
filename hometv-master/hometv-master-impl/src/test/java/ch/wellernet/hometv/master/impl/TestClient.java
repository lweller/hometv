package ch.wellernet.hometv.master.impl;

import static org.restlet.data.Protocol.HTTP;

import org.restlet.Client;
import org.restlet.resource.ClientResource;

import ch.wellernet.hometv.master.api.service.ChannelRessource;

public class TestClient {
    public static void main(String... args) {
        Client client = new Client(HTTP);
        ClientResource root = new ClientResource("http://localhost:8080/");
        root.setNext(client);
        root.getChild("channels/").getChild("1", ChannelRessource.class).pause();
    }
}
