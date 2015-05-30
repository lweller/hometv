package ch.wellernet.hometv.util.restlet;

import org.restlet.resource.ClientResource;

public class ClientRessourceFactory<ID, T> {
    private final ClientResource clientResource;
    private final Class<T> type;

    public ClientRessourceFactory(ClientResource clientResource, Class<T> type) {
        this.clientResource = clientResource;
        this.type = type;
    }

    public T getRessource(ID id) {
        return clientResource.getChild(id.toString(), type);
    }
}
