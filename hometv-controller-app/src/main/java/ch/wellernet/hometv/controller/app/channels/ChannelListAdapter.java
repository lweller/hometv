package ch.wellernet.hometv.controller.app.channels;

import static ch.wellernet.hometv.controller.app.channels.ChannelDetailView_.build;
import static ch.wellernet.hometv.controller.app.channels.ChannelHeaderView_.build;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static roboguice.RoboGuice.getInjector;

import java.util.List;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import ch.wellernet.hometv.master.api.model.Channel;
import ch.wellernet.hometv.master.api.service.ChannelsRessource;

import com.google.inject.Inject;

@EBean
public class ChannelListAdapter extends BaseExpandableListAdapter {

    private List<Channel> channels;

    @Inject
    private Context context;

    @Inject
    private ChannelsRessource channelsRessource;

    public ChannelListAdapter(Context context) {
        this.channels = emptyList();
        getInjector(context).injectMembersWithoutViews(this);
    }

    public List<Channel> getChannels() {
        return unmodifiableList(channels);
    }

    @Override
    public Channel getChild(int groupPosition, int childPosititon) {
        return channels.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
            ViewGroup parent) {
        ChannelDetailView view = convertView == null ? build(context) : (ChannelDetailView) convertView;
        view.setChannel(getChild(groupPosition, childPosition));
        return view;
    }

    @Override
    public Channel getGroup(int groupPosition) {
        return channels.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return channels.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ChannelHeaderView view = convertView == null ? build(context, groupPosition) : (ChannelHeaderView) convertView;
        view.setChannel(getGroup(groupPosition));
        return view;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public synchronized void setChannels(List<Channel> channels) {
        this.channels = channels;
        refresh();
    }

    @UiThread
    void refresh() {
        notifyDataSetChanged();
    }

    @Background
    @AfterInject
    void reload() {
        setChannels(channelsRessource.load());
    }
}
