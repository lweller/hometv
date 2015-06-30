/**
 *
 */
package ch.wellernet.hometv.master.impl.vlc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties in application.properties to configure VLC subcomponent.
 *
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Component
@ConfigurationProperties("vlc")
public class VlcProperties {
    /**
     * <code>vlc.hostname</code> : host where VLC media player is running, defaults to <code>localhost</code>
     */
    private String hostname = "localhost";

    /**
     * <code>vlc.port</code> : port used to connect to (and startup) VLC media player, defaults to <code>4212</code>
     */
    private int port = 4212;

    /**
     * <code>vlc.stream_port</code> : port used by VLC media player to stream items, defaults to <code>8080</code>
     */
    private int streamPort = 8080;

    /**
     * <code>vlc.password</code> : password used to connect to (and startup) VLC media player, defaults to <code>something</code>
     */
    private char[] password = "something".toCharArray();

    /**
     * <code>vlc.startVlc</code> : determines if VLC media player should be started, defaults to <code>true</code>
     */
    private boolean startVlc = true;

    /**
     * <code>vlc.excutable</code> : path to command line executable of VLC media player, defaults to <code>/usr/bin/vlc-wrapper</code>
     */
    private String executable = "/usr/bin/vlc-wrapper";

    public String getExecutable() {
        return executable;
    }

    public String getHostname() {
        return hostname;
    }

    public char[] getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public int getStreamPort() {
        return streamPort;
    }

    public boolean isStartVlc() {
        return startVlc;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setStartVlc(boolean startVlc) {
        this.startVlc = startVlc;
    }

}
