package ch.wellernet.hometv.controller.app;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import ch.wellernet.hometv.controller.app.channels.ChannelAdministrationActivity_;

@EActivity(R.layout.activity_controller_overview)
public class ControllerOverviewActivity extends Activity {

    @Click(R.id.button_channel_administration)
    public void openChannelList(View view) {
        startActivity(new Intent(this, ChannelAdministrationActivity_.class));
    }
}
