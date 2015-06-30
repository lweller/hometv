package ch.wellernet.hometv.master.api.model;

import static java.util.Collections.unmodifiableList;
import static javax.persistence.CascadeType.DETACH;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;

import org.joda.time.Duration;

import ch.wellernet.restlet.util.JsonObject;

@Embeddable
public class PlayList implements JsonObject {

    private static final long serialVersionUID = 1L;

    @ManyToMany(cascade = DETACH)
    @OrderColumn(name = "ORDER")
    @JoinTable(schema = "HOMETV", name = "PLAY_LIST", joinColumns = @JoinColumn(name = "ID_CHANNEL"), inverseJoinColumns = @JoinColumn(name = "ID_PLAY_LIST_ITEM"))
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
