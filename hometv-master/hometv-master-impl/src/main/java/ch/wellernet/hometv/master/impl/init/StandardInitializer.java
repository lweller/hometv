/**
 *
 */
package ch.wellernet.hometv.master.impl.init;

import static java.lang.Math.pow;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

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

    private static final String VLC_EXEC_COMAND = "%s -I telnet --telnet-port=%d --telnet-password=%s";

    @Resource
    private PlatformTransactionManager transactionManager;

    @Resource
    private VlcManager vlcManager;

    @Resource
    private VlcProperties properties;

    @Autowired(required = false)
    private DataInitializer dataInitializer;

    private Process vlcProcess;

    @PostConstruct
    public void setup() throws IOException, VlcConnectionException {
        new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                LOG.info("Initializing default setup");

                if (properties.isStartVlc()) {
                    String command = format(VLC_EXEC_COMAND, properties.getExecutable(), properties.getPort(), new String(properties.getPassword()));
                    LOG.debug(format("executing command: %s", command));
                    try {
                        vlcProcess = getRuntime().exec(command);
                    } catch (IOException exception) {
                        LOG.warn("Caught exception", exception);
                        return;
                    }
                    if (vlcUpAndRunning()) {
                        LOG.info("VLC media player sucessfully started");
                    } else {
                        LOG.info("Aborting setup");
                        return;
                    }
                }

                try {
                    vlcManager.connect(properties.getPassword());
                } catch (VlcConnectionException exception) {
                    LOG.warn("Caught exception", exception);
                    return;
                }
                LOG.info("Successfully connected to VLC mediaplayer");

                if (dataInitializer != null) {
                    dataInitializer.initData();
                    LOG.info("Initialized data");
                }
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        if (vlcProcess != null) {
            vlcProcess.destroy();
            LOG.info("VLC media player successfully stopped");
        }
    }

    private synchronized boolean vlcUpAndRunning() {
        Socket socket = null;
        try {
            for (int i = 0; i < 5; i++) {
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress("localhost", vlcManager.getPort()));
                    return true;
                } catch (ConnectException exception) {
                    long delay = (long) (1000 * pow(2, i));
                    LOG.debug(format("VLC media player actually not runnning, retrying in %d seconds", delay / 1000));
                    wait(delay);
                } finally {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                    socket = null;
                }
            }
            LOG.warn("VLC media player seems not up and runnning");
            return false;
        } catch (IOException | InterruptedException exception) {
            LOG.warn("Cauth exeception", exception);
            return false;
        }

    }
}
