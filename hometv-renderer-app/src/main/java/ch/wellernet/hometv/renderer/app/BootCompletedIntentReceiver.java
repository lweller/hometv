/**
 *
 */
package ch.wellernet.hometv.renderer.app;

import roboguice.receiver.RoboBroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import ch.wellernet.hometv.renderer.app.service.RendererService_;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public class BootCompletedIntentReceiver extends RoboBroadcastReceiver {

    /**
     * @see roboguice.receiver.RoboBroadcastReceiver#handleReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void handleReceive(Context context, Intent intent) {
        Log.e("hometv", "boot");
        context.startService(new Intent(context, RendererService_.class));
    }
}
