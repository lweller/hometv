package ch.wellernet.hometv.master.api.model;

import org.joda.time.Duration;

import ch.wellernet.hometv.util.model.IdentifyableObject;

public class PlayListItem extends IdentifyableObject<Integer> {

    private static final long serialVersionUID = 1L;

    private String title;

    private Duration duration;

    public PlayListItem(int id) {
        super(id);
    }

    private PlayListItem() {
        super(null);
    }

    public Duration getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
