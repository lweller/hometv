/**
 *
 */
package ch.wellernet.hometv.renderer.app.receiver;

import roboguice.receiver.RoboBroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import ch.wellernet.hometv.renderer.app.controller.RendererController;
import ch.wellernet.hometv.renderer.app.controller.events.RegisterEvent;
import ch.wellernet.hometv.renderer.app.controller.events.StartServiceEvent;

import com.google.inject.Inject;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public class BootCompletedReceiver extends RoboBroadcastReceiver {

    @Inject
    private RendererController rendererController;

    /**
     * @see roboguice.receiver.RoboBroadcastReceiver#handleReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void handleReceive(Context context, Intent intent) {
        Log.e("hometv", "boot");
        rendererController.startService(new StartServiceEvent());
        rendererController.register(new RegisterEvent());
    }
}
