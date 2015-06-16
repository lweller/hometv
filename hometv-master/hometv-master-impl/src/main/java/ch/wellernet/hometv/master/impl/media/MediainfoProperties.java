/**
 *
 */
package ch.wellernet.hometv.master.impl.media;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Component
@ConfigurationProperties("mediainfo")
public class MediainfoProperties {
    /**
     * <code>mediainfo.executable</code> : path to command line executable of mediainfo utility, defaults to <code>/usr/bin/mediainfo</code>
     */
    private String executable = "/usr/bin/mediainfo";

    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }
}
