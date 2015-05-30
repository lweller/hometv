package ch.wellernet.hometv.controller.app.channels;

import static ch.wellernet.hometv.master.api.model.ChannelRestartMode.FROM_BEGIN_OF_LAST_ITEM;
import static ch.wellernet.hometv.master.api.model.ChannelRestartMode.FROM_BEGIN_OF_PLAY_LIST;
import static ch.wellernet.hometv.master.api.model.ChannelRestartMode.FROM_LAST_POSITION;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.wellernet.hometv.controller.app.R;
import ch.wellernet.hometv.master.api.model.ChannelRestartMode;

@EFragment(R.layout.dialog_restart_mode)
public class RestartModeDialog extends DialogFragment {

    public interface OnReturnListener {
        public void onReturn(ChannelRestartMode restartMode);
    }

    private OnReturnListener listener;

    public OnReturnListener getListener() {
        return listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.askForRestartMode);
        return null;
    }

    public void setListener(OnReturnListener listener) {
        this.listener = listener;
    }

    @Click(R.id.fromLastPositionButton)
    void fromBeginOfLastItem() {
        listener.onReturn(FROM_BEGIN_OF_LAST_ITEM);
        dismiss();
    }

    @Click(R.id.fromBeginOfLastItemButton)
    void fromLastBeginOfPLayList() {
        listener.onReturn(FROM_BEGIN_OF_PLAY_LIST);
        dismiss();
    }

    @Click(R.id.fromBeginOfPlayListButton)
    void fromLastPosition() {
        listener.onReturn(FROM_LAST_POSITION);
        dismiss();
    }
}
