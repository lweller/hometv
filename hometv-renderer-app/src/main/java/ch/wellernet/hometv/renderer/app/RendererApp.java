package ch.wellernet.hometv.renderer.app;

import static roboguice.RoboGuice.DEFAULT_STAGE;
import static roboguice.RoboGuice.getOrCreateBaseApplicationInjector;
import static roboguice.RoboGuice.newDefaultRoboModule;
import static roboguice.RoboGuice.setUseAnnotationDatabases;
import android.app.Application;

import com.google.inject.Binder;
import com.google.inject.Module;

public class RendererApp extends Application {
    public static class Config implements Module {

        /**
         * @see com.google.inject.Module#configure(com.google.inject.Binder)
         */
        @Override
        public void configure(Binder binder) {
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUseAnnotationDatabases(false);
        getOrCreateBaseApplicationInjector(this, DEFAULT_STAGE, newDefaultRoboModule(this), new Config());
    }
}
