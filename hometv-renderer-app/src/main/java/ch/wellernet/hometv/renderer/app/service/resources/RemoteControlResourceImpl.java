/**
 *
 */
package ch.wellernet.hometv.renderer.app.service.resources;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_HOME;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.net.Uri.parse;

import org.restlet.resource.ServerResource;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import ch.wellernet.hometv.common.api.model.RendererInfo;
import ch.wellernet.hometv.renderer.api.service.RemoteControlResource;
import ch.wellernet.hometv.renderer.app.RendererInfoHelper;

import com.google.inject.Inject;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */

public class RemoteControlResourceImpl extends ServerResource implements RemoteControlResource {

    @Inject
    private Context context;

    @Inject
    private RendererInfoHelper rendererInfoHelper;

    /**
     * @see ch.wellernet.hometv.renderer.api.service.RemoteControlResource#getInfos()
     */
    @Override
    public RendererInfo getInfos() {
        return rendererInfoHelper.getRendererInfo();
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
        Uri uri = parse(url);
        Intent intent = new Intent(ACTION_VIEW, uri);
        intent.setDataAndType(uri, "video/*");
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * @see ch.wellernet.hometv.renderer.api.service.RemoteControlResource#stop()
     */
    @Override
    public void stop() {
        Intent intent = new Intent(ACTION_MAIN);
        intent.addCategory(CATEGORY_HOME);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}