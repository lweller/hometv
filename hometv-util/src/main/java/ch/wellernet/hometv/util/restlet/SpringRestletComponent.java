package ch.wellernet.hometv.util.restlet;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.engine.Engine;
import org.restlet.engine.converter.ConverterHelper;

public class SpringRestletComponent extends Component {

    public void setConverterReplacements(Map<Class<? extends ConverterHelper>, ConverterHelper> replacements) {
        List<ConverterHelper> converters = Engine.getInstance().getRegisteredConverters();
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

    public void setServer(Server server) {
        getServers().add(server);
    }

    @Override
    @PostConstruct
    public void start() throws Exception {
        super.start();
    }
}
