package ch.wellernet.hometv.util.restlet;

import javax.annotation.PostConstruct;

import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ServerRessourceHandler extends ServerResource implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @PostConstruct
    private void register() {
        for (String beanName : context.getBeanNamesForAnnotation(Restlet.class)) {
            Restlet restlet = context.getType(beanName).getAnnotation(Restlet.class);
            Router router = (Router) context.getBean(restlet.router());
            router.attach(restlet.uriTemplate(), new SpringRestlet(context, beanName));
        }
    }
}
