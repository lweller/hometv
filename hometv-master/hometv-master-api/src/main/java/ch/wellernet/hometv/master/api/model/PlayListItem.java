package ch.wellernet.hometv.master.api.model;

import java.io.File;

import org.joda.time.Duration;

import ch.wellernet.hometv.util.model.IdentifyableObject;

public class PlayListItem extends IdentifyableObject<Integer> {

    private static final long serialVersionUID = 1L;

    private String title;
    private File file;
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

    public File getFile() {
        return file;
    }

    public String getTitle() {
        return title;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
