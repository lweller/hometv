package ch.wellernet.hometv.controller.app.playlist;

import static ch.wellernet.hometv.controller.app.playlist.PlayListItemEntryView_.build;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static roboguice.RoboGuice.getInjector;

import java.util.List;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import ch.wellernet.hometv.master.api.model.PlayListItem;
import ch.wellernet.hometv.master.api.service.PlayListItemsRessource;

import com.google.inject.Inject;

@EBean
public class PlayListItemListAdapter extends BaseAdapter {

    @Inject
    private Context context;

    @Inject
    private PlayListItemsRessource playListItemsRessource;

    private List<PlayListItem> playListItems;

    public PlayListItemListAdapter(Context context) {
        this.playListItems = emptyList();
        getInjector(context).injectMembersWithoutViews(this);
    }

    @Override
    public int getCount() {
        return playListItems.size();
    }

    @Override
    public PlayListItem getItem(int position) {
        return playListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return playListItems.get(position).getId();
    }

    public List<PlayListItem> getPlayListItems() {
        return unmodifiableList(playListItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlayListItemEntryView view = convertView == null ? build(context) : (PlayListItemEntryView) convertView;
        view.setPlayListItem(getItem(position));
        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public synchronized void setPlayListItems(List<PlayListItem> playListItems) {
        this.playListItems = playListItems;
        refresh();
    }

    @UiThread
    @AfterViews
    void refresh() {
        notifyDataSetChanged();
    }

    @Background
    @AfterInject
    void reload() {
        setPlayListItems(playListItemsRessource.load());
    }

}
