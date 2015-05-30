package ch.wellernet.hometv.controller.app.channels;

import static ch.wellernet.hometv.controller.app.RequestCodes.SATRT_EDIT_PLAY_LIST;
import static ch.wellernet.hometv.controller.app.playlist.EditPlayListActivity.EXTRA_CHANNEL_ID;
import static ch.wellernet.hometv.master.api.model.ChannelState.IDLE;
import static ch.wellernet.hometv.master.api.model.ChannelState.PLAYING;
import static roboguice.RoboGuice.getInjector;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import roboguice.event.EventManager;
import roboguice.event.Observes;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.RelativeLayout;
import ch.wellernet.hometv.controller.app.R;
import ch.wellernet.hometv.controller.app.channels.RestartModeDialog.OnReturnListener;
import ch.wellernet.hometv.controller.app.channels.events.ChannelChangedEvent;
import ch.wellernet.hometv.controller.app.channels.events.ChannelPauseEvent;
import ch.wellernet.hometv.controller.app.channels.events.ChannelRestartEvent;
import ch.wellernet.hometv.controller.app.channels.events.ChannelResumeEvent;
import ch.wellernet.hometv.controller.app.channels.events.ChannelStopEvent;
import ch.wellernet.hometv.controller.app.playlist.EditPlayListActivity_;
import ch.wellernet.hometv.master.api.model.Channel;
import ch.wellernet.hometv.master.api.model.ChannelRestartMode;

import com.google.inject.Inject;

@EViewGroup(R.layout.view_channel_detail)
public class ChannelDetailView extends RelativeLayout {

    private Channel channel;

    @Inject
    private Activity activity;

    @Inject
    private EventManager eventManager;

    @ViewById
    Button selectButton, playPauseButton, stopButton;

    @StringRes
    String play, pause;

    ChannelDetailView(Context context) {
        super(context);
        getInjector(context).injectMembersWithoutViews(this);
    }

    public Channel getChannel() {
        return channel;
    }

    public void onChannelChanged(@Observes ChannelChangedEvent event) {
        Channel oldChannel = getChannel();
        Channel newChannel = event.getChannel();
        if (newChannel != null && newChannel.getId() == oldChannel.getId()) {
            setChannel(newChannel);
        }
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
        refresh();
    }

    @Override
    protected void onAttachedToWindow() {
        setVisibility(INVISIBLE);
        super.onAttachedToWindow();
    }

    @Click(R.id.playPauseButton)
    void onPlayPauseClicked() {
        switch (getChannel().getState()) {
        case PLAYING:
            eventManager.fire(new ChannelPauseEvent(getChannel().getId()));
            break;
        case PAUSED:
            eventManager.fire(new ChannelResumeEvent(getChannel().getId()));
            break;
        case STOPPED:
            RestartModeDialog dialog = RestartModeDialog_.builder().build();
            dialog.setListener(new OnReturnListener() {
                @Override
                public void onReturn(ChannelRestartMode restartMode) {
                    eventManager.fire(new ChannelRestartEvent(getChannel().getId(), restartMode));
                }
            });
            dialog.show(((Activity) getContext()).getFragmentManager(), "dialog");

            break;
        default:
        }
    }

    @Click(R.id.selectButton)
    void onSelectClicked() {
        Intent intent = new Intent(getContext(), EditPlayListActivity_.class);
        intent.putExtra(EXTRA_CHANNEL_ID, getChannel().getId());
        activity.startActivityForResult(intent, SATRT_EDIT_PLAY_LIST);
    }

    @Click(R.id.stopButton)
    void onStopClicked() {
        eventManager.fire(new ChannelStopEvent(getChannel().getId()));
    }

    @UiThread
    void refresh() {
        Channel channel = getChannel();
        if (channel == null) {
            setVisibility(INVISIBLE);
        } else {
            selectButton.setEnabled(true);

            playPauseButton.setEnabled(channel.getState() != IDLE);
            playPauseButton.setText(channel.getState() == PLAYING ? pause : play);

            stopButton.setEnabled(channel.getState() != IDLE);

            setVisibility(VISIBLE);
        }
    }
}
