package ch.wellernet.hometv.util.restlet;

import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Status;
import org.restlet.resource.ServerResource;
import org.springframework.context.ApplicationContext;

public class SpringRestlet extends Restlet {

    private final ApplicationContext applicationContext;
    private final String target;

    public SpringRestlet(ApplicationContext applicationContext, String target) {
        this.applicationContext = applicationContext;
        this.target = target;
    }

    /**
     * @see org.restlet.Restlet#handle(org.restlet.Request, org.restlet.Response)
     */
    @Override
    public void handle(Request request, Response response) {
        super.handle(request, response);

        if (isStarted()) {
            ServerResource targetResource = find();

            if (targetResource == null) {
                // If the current status is a success but we couldn't
                // find the target resource for the request's URI,
                // then we set the response status to 404 (Not Found).
                if (getLogger().isLoggable(Level.WARNING)) {
                    getLogger().warning("No target resource was defined for this finder: " + toString());
                }

                response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            } else {
                targetResource.init(getContext(), request, response);

                if (response == null || response.getStatus().isSuccess()) {
                    targetResource.handle();
                } else {
                    // Probably during the instantiation of the target
                    // server resource, or earlier the status was
                    // changed from the default one. Don't go further.
                }

                targetResource.release();
            }
        }
    }

    private ServerResource find() {
        Object bean = applicationContext.getBean(target);
        return bean instanceof ServerResource ? (ServerResource) bean : null;
    }
}
