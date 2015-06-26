/**
 *
 */
package ch.wellernet.hometv.renderer.app.service.resources;

import org.restlet.resource.ServerResource;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.telephony.TelephonyManager;
import ch.wellernet.hometv.renderer.api.model.RendererInfo;
import ch.wellernet.hometv.renderer.api.service.RemoteControlResource;

import com.google.inject.Inject;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */

public class RemoteControlResourceImpl extends ServerResource implements RemoteControlResource {

    @Inject
    private Context context;

    /**
     * @see ch.wellernet.hometv.renderer.api.service.RemoteControlResource#getInfos()
     */
    @Override
    public RendererInfo getInfos() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        BluetoothAdapter buetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return new RendererInfo(telephonyManager.getDeviceId(), buetoothAdapter == null ? "unknown" : buetoothAdapter.getName(), "unknown");
    }

    /**
     * @see ch.wellernet.hometv.renderer.api.service.RemoteControlResource#ping()
     */
    @Override
    public void ping() {
        // just return to show that I'm alive
    }

    /**
     * @see ch.wellernet.hometv.renderer.api.service.RemoteControlResource#play(java.lang.String)
     */
    @Override
    public void play(String url) {
        // TODO Auto-generated method stub
    }
}