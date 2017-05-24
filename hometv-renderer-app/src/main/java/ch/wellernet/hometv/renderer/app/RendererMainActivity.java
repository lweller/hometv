package ch.wellernet.hometv.renderer.app;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.RoboGuice;

import roboguice.event.EventManager;
import android.app.Activity;
import android.view.View;
import ch.wellernet.hometv.renderer.app.controller.RendererController;
import ch.wellernet.hometv.renderer.app.controller.events.RegisterEvent;
import ch.wellernet.hometv.renderer.app.controller.events.StartServiceEvent;

import com.google.inject.Inject;

@EActivity(R.layout.activity_renderer_main)
@RoboGuice(RendererController.class)
public class RendererMainActivity extends Activity {

    @Inject
    private EventManager eventManager;

    @Click(R.id.button_register)
    public void register(View view) {
        eventManager.fire(new RegisterEvent());
    }

    @Click(R.id.button_start_service)
    public void startRendererService(View view) {
        eventManager.fire(new StartServiceEvent());
    }
}
