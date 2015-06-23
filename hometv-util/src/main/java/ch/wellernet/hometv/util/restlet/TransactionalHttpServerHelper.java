/**
 *
 */
package ch.wellernet.hometv.util.restlet;

import static java.lang.String.format;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

import org.restlet.Server;
import org.restlet.engine.adapter.ServerCall;
import org.restlet.engine.connector.HttpServerHelper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Extension of standard {@link HttpServerHelper} so that each call is handled in a new transaction.
 *
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public class TransactionalHttpServerHelper extends HttpServerHelper {

    public static final String TRANSACTION_MANAGER = "transactionManager";

    /**
     * @param server
     */
    public TransactionalHttpServerHelper(Server server) {
        super(server);
    }

    /**
     * @see org.restlet.engine.adapter.HttpServerHelper#handle(org.restlet.engine.adapter.ServerCall)
     */
    @Override
    public void handle(final ServerCall httpCall) {
        Object transactionManager = getContext().getAttributes().get(TRANSACTION_MANAGER);
        if (transactionManager == null) {
            throw new IllegalStateException(format("Restlet server context should contain platform trasnaction manager under key %s",
                    TRANSACTION_MANAGER));
        }
        if (!(transactionManager instanceof PlatformTransactionManager)) {
            throw new IllegalStateException(format("Restlet server context contains an object of type %s instead of %s",
                    transactionManager.getClass(), PlatformTransactionManager.class));
        }

        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        new TransactionTemplate((PlatformTransactionManager) transactionManager, transactionDefinition)
                .execute(new TransactionCallbackWithoutResult() {

                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        TransactionalHttpServerHelper.super.handle(httpCall);
                    }
                });
    }
}
