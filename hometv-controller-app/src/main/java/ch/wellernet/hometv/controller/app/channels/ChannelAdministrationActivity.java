package ch.wellernet.hometv.controller.app.channels;

import static ch.wellernet.hometv.controller.app.RequestCodes.SATRT_EDIT_PLAY_LIST;
import static ch.wellernet.hometv.controller.app.playlist.EditPlayListActivity.EXTRA_CHANNEL_ID;
import static ch.wellernet.hometv.controller.app.playlist.EditPlayListActivity.EXTRA_PLAY_LIST_ITEM_IDS;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OnActivityResult.Extra;
import org.androidannotations.annotations.RoboGuice;
import org.androidannotations.annotations.SupposeUiThread;
import org.androidannotations.annotations.ViewById;

import roboguice.event.EventManager;
import android.app.Activity;
import android.content.Intent;
import android.widget.ExpandableListView;
import ch.wellernet.hometv.controller.app.R;
import ch.wellernet.hometv.controller.app.channels.events.ChannelStartEvent;

import com.google.inject.Inject;

@EActivity(R.layout.activity_channel_administration)
@RoboGuice(ChannelController.class)
public class ChannelAdministrationActivity extends Activity {

    @Inject
    private EventManager eventManager;

    @ViewById
    ExpandableListView channelList;

    @Bean
    ChannelListAdapter channelListAdapter;

    @OnActivityResult(SATRT_EDIT_PLAY_LIST)
    void onPlayListDefined(Intent intent, int resultCode, @Extra(EXTRA_CHANNEL_ID) int channelId,
            @Extra(EXTRA_PLAY_LIST_ITEM_IDS) int[] playListItemsIds) {
        eventManager.fire(new ChannelStartEvent(channelId, playListItemsIds));
    }

    @SupposeUiThread
    @AfterViews
    void setup() {
        channelList.setAdapter(channelListAdapter);
    }

}