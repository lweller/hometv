/**
 *
 */
package ch.wellernet.hometv.master.impl.init;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Component
@ConfigurationProperties("default")
public class DefaultDataProperties {
    public static final int DEFUALT_CHANNEL_COUNT = 2;
    public static final String DEFAULT_MEDIA_DIRECTORY = "/var/lib/hometv-master-server/medias";

    /**
     * <code>vlc.channel_count</code> : number of channels to create on initial startup, defaults to {@value #DEFUALT_CHANNEL_COUNT}
     */
    private int channelCount = DEFUALT_CHANNEL_COUNT;

    /**
     * <code>vlc.media_directoty</code> : directory to scan for media items startup, defaults to {@value #DEFAULT_MEDIA_DIRECTORY}
     */
    private File mediaDirectory = new File(DEFAULT_MEDIA_DIRECTORY);

    public int getChannelCount() {
        return channelCount;
    }

    public File getMediaDirectory() {
        return mediaDirectory;
    }

    public void setChannelCount(int channelCount) {
        this.channelCount = channelCount;
    }

    public void setMediaDirectory(File mediaDirectory) {
        this.mediaDirectory = mediaDirectory;
    }
}
