/**
 *
 */
package ch.wellernet.hometv.master.impl.media;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.wellernet.mediainfo.Mediainfo;

/**
 * Spring configuration for media subcomponent.
 *
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Configuration
public class MediaConfiguration {

    @Resource
    private MediainfoProperties properties;

    @Bean
    public Mediainfo mediainfo() {
        return new Mediainfo(properties.getExecutable());
    }
}
