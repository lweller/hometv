/**
 *
 */
package ch.wellernet.hometv.master.impl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties in application.properties to configure http server.
 *
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Component
@ConfigurationProperties("server")
public class ServerProperties {

    /**
     * <code>server.port</code> : port where http server should listen for client ocnnections, defaults to <code>8100</code>
     */
    private int port = 8100;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
