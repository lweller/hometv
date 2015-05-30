package ch.wellernet.hometv.master.api.model;

import java.util.List;

import org.restlet.util.WrapperList;

public class PlayListItemList extends WrapperList<PlayListItem> {

    public PlayListItemList() {
        super();
    }

    public PlayListItemList(int initialCapacity) {
        super(initialCapacity);
    }

    public PlayListItemList(List<PlayListItem> delegate) {
        super(delegate);
    }
}
