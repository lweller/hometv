/**
 *
 */
package ch.wellernet.hometv.master.impl.init;

import static org.h2.tools.Server.createTcpServer;

import org.h2.tools.Server;

import ch.wellernet.hometv.master.impl.Main;

/**
 * Main class wrapper to start HomeTV Master in test environment.
 *
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public class TestMain {
    public static void main(String... args) throws Exception {
        Main.main(args);

        Server server = createTcpServer("-tcpPort", "9092", "-tcpAllowOthers");
        server.start();

        System.out.println("*************************************************************************************");
        System.out.println("Database connection started at: " + server.getURL());
        System.out.println("*************************************************************************************");
        System.out.println("Press enter to shutdown HomeTV Master garcefully.");
        System.out.println("*************************************************************************************");
        System.in.read();
        System.exit(0);
    }
}
