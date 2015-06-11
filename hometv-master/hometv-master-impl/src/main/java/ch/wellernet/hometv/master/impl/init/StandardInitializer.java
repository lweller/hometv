/**
 *
 */
package ch.wellernet.hometv.master.impl.init;

import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import ch.wellernet.hometv.master.impl.vlc.VlcProperties;
import ch.wellernet.vlclib.VlcConnectionException;
import ch.wellernet.vlclib.VlcManager;

/**
 * Does some initializing tasks at the end of startup process. Especially it will start the VLC media player (if <code>vlc.startVlc=true</code> in
 * applicaiton.properties) and tries to connect to it.
 *
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Configuration
public class StandardInitializer {

    private static final Log LOG = LogFactory.getLog(StandardInitializer.class);

    private static final String VLC_EXEC_COMAND = "%s -I telnet --telnet-password=%s";

    @Resource
    private VlcManager vlcManager;

    @Resource
    private VlcProperties properties;

    @Autowired(required = false)
    private DataInitializer dataInitializer;

    private Process vlcProcess;

    @PostConstruct
    public void setup() throws IOException, VlcConnectionException {
        LOG.info("Initializing default setup");

        if (properties.isStartVlc()) {
            vlcProcess = getRuntime().exec(format(VLC_EXEC_COMAND, properties.getVlcExecutable(), new String(properties.getPassword())));
            LOG.info("VLC media player sucessfully started");
        }

        vlcManager.connect(properties.getPassword());
        LOG.info("Successfully connected to VLC mediaplayer");

        if (dataInitializer != null) {
            dataInitializer.initData();
            LOG.info("Initialized data");
        }
    }

    @PreDestroy
    public void shutdown() {
        if (vlcProcess != null) {
            vlcProcess.destroy();
            LOG.info("VLC media player successfully stopped");
        }
    }
}
