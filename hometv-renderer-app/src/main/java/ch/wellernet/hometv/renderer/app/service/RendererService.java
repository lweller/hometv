/**
 *
 */
package ch.wellernet.hometv.renderer.app.service;

import static org.restlet.data.Protocol.HTTP;

import org.androidannotations.annotations.EService;
import org.restlet.Component;
import org.restlet.routing.Router;

import roboguice.service.RoboService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import ch.wellernet.hometv.renderer.app.service.resources.RemoteControlResourceImpl;
import ch.wellernet.restlet.android.RoboGuiceRestlet;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@EService
public class RendererService extends RoboService {
    public class Binder extends android.os.Binder {
        RendererService getService() {
            return RendererService.this;
        }
    }

    private final IBinder binder = new Binder();

    /**
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intend) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e("hometv", "started");
        Component component = new Component();
        component.getServers().add(HTTP, 8100);
        Router router = new Router();
        component.getDefaultHost().attach(router);
        router.attach("/", new RoboGuiceRestlet(this, RemoteControlResourceImpl.class));
        try {
            component.start();
        } catch (Exception exception) {
            Log.e("hometv", "Caught exception", exception);
        }
        return START_STICKY;
    }

}
