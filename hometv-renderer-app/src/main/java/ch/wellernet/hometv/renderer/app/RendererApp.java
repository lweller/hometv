package ch.wellernet.hometv.renderer.app;

import static roboguice.RoboGuice.DEFAULT_STAGE;
import static roboguice.RoboGuice.getOrCreateBaseApplicationInjector;
import static roboguice.RoboGuice.newDefaultRoboModule;
import static roboguice.RoboGuice.setUseAnnotationDatabases;

import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.ext.nio.HttpServerHelper;

import android.app.Application;
import ch.wellernet.hometv.renderer.api.service.RemoteControlResource;
import ch.wellernet.hometv.renderer.app.controller.RendererController;
import ch.wellernet.hometv.renderer.app.service.resources.RemoteControlResourceImpl;

import com.google.inject.Binder;
import com.google.inject.Module;

public class RendererApp extends Application {
    public static class Config implements Module {

        /**
         * @see com.google.inject.Module#configure(com.google.inject.Binder)
         */
        @Override
        public void configure(Binder binder) {
            Engine.getInstance().getRegisteredConverters().add(new JacksonConverter());
            Engine.getInstance().getRegisteredServers().add(new HttpServerHelper(null));
            binder.bind(RendererController.class).toProvider(RendererController.Provider.class);
            binder.bind(RemoteControlResource.class).to(RemoteControlResourceImpl.class);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUseAnnotationDatabases(false);
        getOrCreateBaseApplicationInjector(this, DEFAULT_STAGE, newDefaultRoboModule(this), new Config());
    }
}
