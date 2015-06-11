package ch.wellernet.hometv.util.restlet;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.engine.Engine;
import org.restlet.engine.converter.ConverterHelper;
import org.restlet.ext.slf4j.Slf4jLoggerFacade;

public class SpringRestletEngine extends Component {

    public SpringRestletEngine() {
        Engine.getInstance().setLoggerFacade(new Slf4jLoggerFacade());
    }

    public void addServer(Server server) {
        getServers().add(server);
    }

    public void setConverterReplacements(Map<Class<? extends ConverterHelper>, ConverterHelper> replacements) {
        Engine engine = Engine.getInstance();
        List<ConverterHelper> converters = engine.getRegisteredConverters();
        for (ConverterHelper converter : converters) {
            if (replacements.containsKey(converter.getClass())) {
                converters.remove(converter);
                converters.add(replacements.get(converter.getClass()));
            }
        }
    }

    public void setRoot(Restlet root) {
        getDefaultHost().attach(root);
    }

    @Override
    @PostConstruct
    public void start() throws Exception {
        super.start();
    }
}
