package ch.wellernet.hometv.controller.app.playlist;

import static java.lang.String.format;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.joda.time.Period;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.wellernet.hometv.controller.app.R;
import ch.wellernet.hometv.master.api.model.PlayListItem;

@EViewGroup(R.layout.view_play_list_item_entry)
public class PlayListItemEntryView extends LinearLayout implements Checkable {

    @ViewById
    TextView titleLabel, durationLabel;

    @ViewById
    CheckBox checkBox;

    @StringRes
    String duration;

    private boolean checked;
    private PlayListItem playListItem;

    public PlayListItemEntryView(Context context) {
        super(context);
        setVisibility(INVISIBLE);
    }

    public PlayListItem getPlayListItem() {
        return playListItem;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public synchronized void setChecked(boolean checked) {
        this.checked = checked;
        refresh();
    }

    public synchronized void setPlayListItem(PlayListItem playListItem) {
        this.playListItem = playListItem;
        refresh();
    }

    @Override
    public synchronized void toggle() {
        setChecked(!checked);
    }

    @UiThread
    @AfterInject
    void refresh() {
        PlayListItem playListItem = getPlayListItem();
        if (playListItem == null) {
            setVisibility(INVISIBLE);
        } else {
            titleLabel.setText(playListItem.getTitle());

            Period period = playListItem.getDuration().toPeriod();
            durationLabel.setText(format(duration, period.getHours(), period.getMinutes(), period.getSeconds()));

            checkBox.setChecked(checked);

            setVisibility(VISIBLE);
        }
    }
}
