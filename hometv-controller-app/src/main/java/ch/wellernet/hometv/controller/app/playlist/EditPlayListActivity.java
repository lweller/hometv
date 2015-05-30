package ch.wellernet.hometv.controller.app.playlist;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.RoboGuice;
import org.androidannotations.annotations.SupposeUiThread;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.widget.ListView;
import ch.wellernet.hometv.controller.app.R;

@EActivity(R.layout.activity_edit_play_list)
@RoboGuice
public class EditPlayListActivity extends Activity {

    public static final String EXTRA_CHANNEL_ID = "channelId";
    public static final String EXTRA_PLAY_LIST_ITEM_IDS = "playListItemIds";

    @Extra(EXTRA_CHANNEL_ID)
    int channelId;

    @ViewById
    ListView playListItemList;

    @Bean
    PlayListItemListAdapter playListItemListAdapter;

    @Click(R.id.ok)
    void onOkClicked() {
        long[] checkedItemIds = playListItemList.getCheckedItemIds();
        int[] playListItemIds = new int[checkedItemIds.length];
        for (int i = 0; i < checkedItemIds.length; i++) {
            playListItemIds[i] = (int) checkedItemIds[i];
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_CHANNEL_ID, channelId);
        data.putExtra(EXTRA_PLAY_LIST_ITEM_IDS, playListItemIds);
        setResult(RESULT_OK, data);
        finish();
    }

    @SupposeUiThread
    @AfterViews
    void setup() {
        playListItemList.setAdapter(playListItemListAdapter);
    }
}
