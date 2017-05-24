/**
 *
 */
package ch.wellernet.hometv.renderer.app.controller;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;
import static java.lang.String.format;
import static org.restlet.data.Protocol.HTTP;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.restlet.Client;
import org.restlet.resource.ClientResource;

import roboguice.RoboGuice;
import roboguice.event.Observes;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import ch.wellernet.hometv.common.api.model.RendererInfo;
import ch.wellernet.hometv.master.api.service.RenderersRessource;
import ch.wellernet.hometv.renderer.app.controller.events.RegisterEvent;
import ch.wellernet.hometv.renderer.app.controller.events.StartServiceEvent;
import ch.wellernet.hometv.renderer.app.service.RendererService_;

import com.google.inject.Inject;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@EBean
public class RendererController {

    public static class Provider implements com.google.inject.Provider<RendererController> {

        @Inject
        private Context context;

        @Override
        public RendererController get() {
            RendererController controller = RendererController_.getInstance_(null);
            RoboGuice.getInjector(context).injectMembersWithoutViews(controller);
            return controller;
        }
    }

    @Inject
    private WifiManager wifiManager;

    @Inject
    private TelephonyManager telephonyManager;

    @Inject
    private Context context;

    public RendererInfo getRendererInfo() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            BluetoothAdapter buetoothAdapter = getDefaultAdapter();
            int ipAddress = wifiInfo.getIpAddress();
            return new RendererInfo(telephonyManager.getDeviceId(), buetoothAdapter == null ? "unknown" : buetoothAdapter.getName(), format(
                    "%d.%d.%d.%d", ipAddress & 0xFF, ipAddress >> 8 & 0xFF, ipAddress >> 16 & 0xFF, ipAddress >> 24 & 0xFF));
        } else {
            return null;
        }
    }

    @Background
    public void register(@Observes RegisterEvent event) {
        Log.e("hometv", "Trying to register on master");
        Client client = new Client(HTTP);
        ClientResource root = new ClientResource("http://192.168.24.10:8100/");
        root.setNext(client);
        root.setEntityBuffering(true);
        RenderersRessource renderersResource = root.getChild("renderers", RenderersRessource.class);
        renderersResource.register(getRendererInfo());
    }

    @UiThread
    public void startService(@Observes StartServiceEvent event) {
        Log.e("hometv", "Starting remote control service");
        context.startService(new Intent(context, RendererService_.class));
    }
}
