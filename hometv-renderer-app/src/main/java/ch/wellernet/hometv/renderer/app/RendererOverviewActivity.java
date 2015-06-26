package ch.wellernet.hometv.renderer.app;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import ch.wellernet.hometv.renderer.app.service.RendererService_;

@EActivity(R.layout.activity_renderer_overview)
public class RendererOverviewActivity extends Activity {

    @Click(R.id.button_start_service)
    public void startRendererService(View view) {
        startService(new Intent(this, RendererService_.class));
    }
}
