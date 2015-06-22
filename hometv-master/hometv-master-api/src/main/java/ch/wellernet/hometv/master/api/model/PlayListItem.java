package ch.wellernet.hometv.master.api.model;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.joda.time.Duration;

import ch.wellernet.hometv.util.model.IdentifyableObject;
import ch.wellernet.hometv.util.model.ModelObjectBuilder;

@Entity
@Table(name = "PLAY_LIST_ITEM", schema = "HOMETV")
@SequenceGenerator(name = "primary_key", schema = "HOMETV", sequenceName = "SEQ_PLAY_LIST_ITEM")
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

    @Column(name = "TITLE")
    private String title;

    @Column(name = "FILE")
    private File file;

    @Column(name = "DURATION")
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
