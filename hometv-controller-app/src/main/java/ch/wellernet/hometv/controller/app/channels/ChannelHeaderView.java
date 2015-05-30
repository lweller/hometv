package ch.wellernet.hometv.controller.app.channels;

import static java.lang.String.format;
import static roboguice.RoboGuice.getInjector;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import roboguice.event.Observes;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.wellernet.hometv.controller.app.R;
import ch.wellernet.hometv.controller.app.channels.events.ChannelChangedEvent;
import ch.wellernet.hometv.master.api.model.Channel;

@EViewGroup(R.layout.view_channel_header)
public class ChannelHeaderView extends LinearLayout {

    private int index;
    private Channel channel;

    @ViewById
    TextView titleLabel, subtitleLabel;

    @StringRes
    String channelTitle, empty, paused, stopped;

    ChannelHeaderView(Context context, int index) {
        super(context);
        this.index = index;
        getInjector(context).injectMembersWithoutViews(this);
    }

    public Channel getChannel() {
        return channel;
    }

    public int getIndex() {
        return index;
    }

    public void onChannelChanged(@Observes ChannelChangedEvent event) {
        Channel oldChannel = getChannel();
        Channel newChannel = event.getChannel();
        if (newChannel != null && newChannel.getId() == oldChannel.getId()) {
            setChannel(newChannel);
        }
    }

    public synchronized void setChannel(Channel channel) {
        this.channel = channel;
        refresh();
    }

    @Override
    protected void onAttachedToWindow() {
        setVisibility(INVISIBLE);
        super.onAttachedToWindow();
    }

    @UiThread
    void refresh() {
        Channel channel = getChannel();

        if (channel == null) {
            setVisibility(INVISIBLE);
        } else {

            titleLabel.setText(format(channelTitle, index + 1));

            String subtitle = empty;
            if (channel != null && channel.getCurrentPlayListItem() != null
                    && channel.getCurrentPlayListItem().getTitle() != null) {
                String playListItemTitle = channel.getCurrentPlayListItem().getTitle();
                switch (channel.getState()) {
                case PLAYING:
                    subtitle = format("%s", playListItemTitle);
                    break;
                case PAUSED:
                    subtitle = format("%s (%s)", playListItemTitle, paused);
                    break;
                case STOPPED:
                    subtitle = format("%s (%s)", playListItemTitle, stopped);
                    break;
                default:
                }
            }
            subtitleLabel.setText(subtitle);

            setVisibility(VISIBLE);
        }
    }
}
