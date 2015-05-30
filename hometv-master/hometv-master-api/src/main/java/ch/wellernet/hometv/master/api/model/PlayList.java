package ch.wellernet.hometv.master.api.model;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Duration;

import ch.wellernet.hometv.util.model.Serializable;

public class PlayList implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<PlayListItem> items;

    public PlayList() {
        items = new ArrayList<PlayListItem>();
    }

    public void addItem(PlayListItem item) {
        items.add(item);
    }

    public void clear() {
        items.clear();

    }

    public List<PlayListItem> getItems() {
        return unmodifiableList(items);
    }

    public Duration getTotalDuration() {
        Duration totalDuration = new Duration(0);
        for (PlayListItem item : items) {
            totalDuration = totalDuration.plus(item.getDuration());
        }
        return totalDuration;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void removeItem(PlayListItem item) {
        items.remove(item);
    }
}
