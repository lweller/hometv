/**
 *
 */
package ch.wellernet.hometv.master.impl.service;

import org.restlet.routing.Router;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;

import ch.wellernet.restlet.spring.RessourceRegistrar;
import ch.wellernet.restlet.spring.Restlet;
import ch.wellernet.restlet.spring.RestletScopeResolver;

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
