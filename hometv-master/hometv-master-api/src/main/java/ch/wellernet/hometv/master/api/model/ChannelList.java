package ch.wellernet.hometv.master.api.model;

import java.util.List;

import org.restlet.util.WrapperList;

public class ChannelList extends WrapperList<Channel> {

    public ChannelList() {
        super();
    }

    public ChannelList(int initialCapacity) {
        super(initialCapacity);
    }

    public ChannelList(List<Channel> delegate) {
        super(delegate);
    }
}