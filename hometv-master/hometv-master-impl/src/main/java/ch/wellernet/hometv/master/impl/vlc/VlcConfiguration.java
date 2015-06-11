/**
 *
 */
package ch.wellernet.hometv.master.impl.vlc;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.wellernet.vlclib.VlcConnectionException;
import ch.wellernet.vlclib.VlcManager;

/**
 * Spring configuration for VLC subcomponent.
 *
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Configuration
public class VlcConfiguration {

    @Resource
    private VlcProperties properties;

    @Bean
    public VlcManager vlcManager() throws VlcConnectionException {
        return new VlcManager(properties.getHostname(), properties.getPort());
    }
}
