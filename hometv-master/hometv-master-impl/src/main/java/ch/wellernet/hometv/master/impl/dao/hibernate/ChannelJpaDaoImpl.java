/**
 *
 */
package ch.wellernet.hometv.master.impl.dao.hibernate;

import org.springframework.stereotype.Repository;

import ch.wellernet.hometv.master.api.model.Channel;
import ch.wellernet.hometv.master.impl.dao.ChannelDao;
import ch.wellernet.hometv.util.dao.AbstractJpaDaoImpl;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Repository
public class ChannelJpaDaoImpl extends AbstractJpaDaoImpl<Integer, Channel> implements ChannelDao {

    /**
     * @see ch.wellernet.hometv.util.dao.AbstractJpaDaoImpl#getType()
     */
    @Override
    protected Class<Channel> getType() {
        return Channel.class;
    }

}
