/**
 *
 */
package ch.wellernet.hometv.master.impl;

import static java.lang.Runtime.getRuntime;
import static org.springframework.boot.SpringApplication.run;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Main class to start HomeTV Master either from command line or with jsvc.
 *
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public class Main implements Daemon {

    private static final Log LOG = LogFactory.getLog(Main.class);

    public static void main(String... args) throws Exception {

        final Main instance = new Main();

        getRuntime().addShutdownHook(new Thread("shutdown") {

            @Override
            public void run() {
                try {
                    instance.stop();
                } catch (Exception exception) {
                    LOG.debug("Caught exception", exception);
                }
                instance.destroy();
            }
        });

        instance.init(null);
        instance.start();
    }

    private ConfigurableApplicationContext applicationContext;

    /**
     * @see org.apache.commons.daemon.Daemon#destroy()
     */
    @Override
    public void destroy() {
    }

    /**
     * @see org.apache.commons.daemon.Daemon#init(org.apache.commons.daemon.DaemonContext)
     */
    @Override
    public void init(DaemonContext context) throws DaemonInitException, Exception {
    }

    /**
     * @see org.apache.commons.daemon.Daemon#start()
     */
    @Override
    public void start() throws Exception {
        LOG.info("Starting HomeTV Master");
        applicationContext = run(MainConfiguration.class);
        LOG.info("HomeTV Master successfully started");
    }

    /**
     * @see org.apache.commons.daemon.Daemon#stop()
     */
    @Override
    public void stop() throws Exception {
        LOG.info("Stopping HomeTV Master");
        applicationContext.close();
        LOG.info("HomeTV Master successfully stopped");
    }
}
