package ch.wellernet.hometv.master.impl.dao.inmemory;

import org.springframework.stereotype.Component;

import ch.wellernet.hometv.master.api.model.Channel;
import ch.wellernet.hometv.master.impl.dao.ChannelDao;
import ch.wellernet.hometv.util.dao.BaseInMemoryGenericDao;
import ch.wellernet.hometv.util.dao.IntegerIdGenerator;

@Component
public class ChannelInMemoryDaoImpl extends BaseInMemoryGenericDao<Integer, Channel> implements ChannelDao {

    public ChannelInMemoryDaoImpl() {
        super(new IntegerIdGenerator<Channel>());
    }

}
