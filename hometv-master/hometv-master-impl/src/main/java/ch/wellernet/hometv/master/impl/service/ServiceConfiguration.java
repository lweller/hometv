/**
 *
 */
package ch.wellernet.hometv.master.impl.service;

import org.restlet.routing.Router;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;

import ch.wellernet.hometv.util.restlet.RestletScopeResolver;
import ch.wellernet.hometv.util.restlet.RessourceRegistrar;
import ch.wellernet.hometv.util.restlet.Restlet;

/**
 * Spring configuration to restlet subcomponent.
 *
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Configuration
@ComponentScan(useDefaultFilters = false, scopeResolver = RestletScopeResolver.class, includeFilters = @Filter(Restlet.class))
public class ServiceConfiguration {

    @Bean
    public RessourceRegistrar ressourceRegistrar() {
        return new RessourceRegistrar();
    }

    @Bean
    public Router root() {
        return new Router();
    }
}
