package ch.wellernet.hometv.controller.app;

import static org.restlet.data.Protocol.HTTP;

import org.restlet.Client;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.resource.ClientResource;

import roboguice.RoboGuice;
import android.app.Application;
import ch.wellernet.hometv.controller.app.channels.ChannelController;
import ch.wellernet.hometv.master.api.service.ChannelRessource;
import ch.wellernet.hometv.master.api.service.ChannelsRessource;
import ch.wellernet.hometv.master.api.service.PlayListItemsRessource;
import ch.wellernet.hometv.util.restlet.ClientRessourceFactory;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class ControllerApp extends Application {
    public static class Config implements Module {

        public static final String CHANNEL_RESSOURCE_FACTORY = "ChannelRessourceFactory";

        @Override
        public void configure(Binder binder) {
            Engine.getInstance().getRegisteredConverters().add(new JacksonConverter());

            Client client = new Client(HTTP);
            ClientResource root = new ClientResource("http://10.0.2.2:8080/");
            root.setNext(client);
            root.setEntityBuffering(true);

            binder.bind(ChannelController.class).toProvider(ChannelController.Provider.class);
            binder.bind(ChannelsRessource.class).toInstance(root.getChild("channels", ChannelsRessource.class));
            binder.bind(PlayListItemsRessource.class).toInstance(
                    root.getChild("playlistitems", PlayListItemsRessource.class));
            binder.bind(new TypeLiteral<ClientRessourceFactory<Integer, ChannelRessource>>() {
            }).toInstance(
                    new ClientRessourceFactory<Integer, ChannelRessource>(root.getChild("channels/"),
                            ChannelRessource.class));
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RoboGuice.setUseAnnotationDatabases(false);
        RoboGuice.getOrCreateBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new Config());
    }
}
