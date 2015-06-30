/**
 *
 */
package ch.wellernet.hometv.renderer.app;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;
import static java.lang.String.format;
import android.bluetooth.BluetoothAdapter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import ch.wellernet.hometv.common.api.model.RendererInfo;

import com.google.inject.Inject;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public class RendererInfoHelper {

    @Inject
    private WifiManager wifiManager;

    @Inject
    private TelephonyManager telephonyManager;

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
}
