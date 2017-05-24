/**
 *
 */
package ch.wellernet.hometv.renderer.app.receiver;

import static android.net.wifi.WifiManager.EXTRA_NETWORK_INFO;
import roboguice.receiver.RoboBroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.util.Log;
import ch.wellernet.hometv.renderer.app.controller.RendererController;
import ch.wellernet.hometv.renderer.app.controller.events.RegisterEvent;

import com.google.inject.Inject;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public class WifiConnectedReceiver extends RoboBroadcastReceiver {

    @Inject
    private RendererController rendererHelper;

    /**
     * @see roboguice.receiver.RoboBroadcastReceiver#handleReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void handleReceive(Context context, Intent intent) {
        Log.e("hometv", "WiFi connected");
        NetworkInfo networkInfo = intent.getParcelableExtra(EXTRA_NETWORK_INFO);
        if (networkInfo.isConnected()) {
            rendererHelper.register(new RegisterEvent());
        }
    }
}
