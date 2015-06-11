/**
 *
 */
package ch.wellernet.hometv.master.impl;

import static org.apache.commons.collections.MapUtils.putAll;
import static org.restlet.data.Protocol.HTTP;

import java.util.HashMap;

import javax.annotation.Resource;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.restlet.Server;
import org.restlet.engine.converter.ConverterHelper;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.routing.Router;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ch.wellernet.hometv.master.impl.service.ServiceConfiguration;
import ch.wellernet.hometv.master.impl.vlc.VlcConfiguration;
import ch.wellernet.hometv.util.restlet.CustomJacksonConverter;
import ch.wellernet.hometv.util.restlet.SpringRestletEngine;

/**
 * Main spring configuration used to start HomeTV Master.
 *
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties
@Import({ VlcConfiguration.class, ServiceConfiguration.class })
public class MainConfiguration {
    @Resource
    private ServerProperties serverProperties;

    @Resource
    private Router root;

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
        return new Server(HTTP, serverProperties.getPort());
    }
}
