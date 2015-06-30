/**
 *
 */
package ch.wellernet.hometv.master.impl;

import static ch.wellernet.restlet.spring.TransactionalHttpServerHelper.TRANSACTION_MANAGER;
import static org.apache.commons.collections.MapUtils.putAll;
import static org.restlet.data.Protocol.HTTP;
import static org.springframework.context.annotation.FilterType.REGEX;

import java.util.HashMap;

import javax.annotation.Resource;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.restlet.Context;
import org.restlet.Server;
import org.restlet.engine.converter.ConverterHelper;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.routing.Router;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ch.wellernet.restlet.spring.SpringRestletEngine;
import ch.wellernet.restlet.util.CustomJacksonConverter;

/**
 * Main spring configuration used to start HomeTV Master.
 *
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Configuration
@ComponentScan(excludeFilters = @Filter(type = REGEX, pattern = ".*\\..*TestConfiguration"))
@EnableConfigurationProperties
@EnableTransactionManagement
public class MainConfiguration {
    @Resource
    private ServerProperties serverProperties;

    @Resource
    private Router root;

    @Resource
    private PlatformTransactionManager transactionManager;

    @Bean
    @SuppressWarnings({ "unchecked" })
    public SpringRestletEngine restletEngine() {
        SpringRestletEngine restletEngine = new SpringRestletEngine();
        restletEngine.addServer(server());
        restletEngine.setRoot(root);
        restletEngine.setConverterReplacements(putAll(new HashMap<Class<? extends ConverterHelper>, ConverterHelper>(),
                new KeyValue[] { new DefaultKeyValue(JacksonConverter.class, new CustomJacksonConverter()) }));
        return restletEngine;
    }

    public Server server() {
        Context context = new Context();
        context.getAttributes().put(TRANSACTION_MANAGER, transactionManager);
        return new Server(context, HTTP, serverProperties.getPort());
    }
}
