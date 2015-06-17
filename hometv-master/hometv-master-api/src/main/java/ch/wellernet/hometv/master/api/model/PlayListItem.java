package ch.wellernet.hometv.master.api.model;

import java.io.File;

import org.joda.time.Duration;

import ch.wellernet.hometv.util.model.IdentifyableObject;
import ch.wellernet.hometv.util.model.ModelObjectBuilder;

public class PlayListItem extends IdentifyableObject<Integer> {

    public static class Builder extends ModelObjectBuilder<Integer, PlayListItem> {
        private PlayListItem instance;

        public Builder(String title, File file, Duration duration) {
            instance = new PlayListItem();
            instance.setTitle(title);
            instance.setFile(file);
            instance.setDuration(duration);
        }

        @Override
        protected PlayListItem build() {
            return instance;
        }
    }

    private static final long serialVersionUID = 1L;

    private String title;

    private File file;
    private Duration duration;

    private PlayListItem() {

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
