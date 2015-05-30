package ch.wellernet.hometv.master.impl.dao.inmemory;

import org.springframework.stereotype.Component;

import ch.wellernet.hometv.master.api.model.PlayListItem;
import ch.wellernet.hometv.master.impl.dao.PlayListItemDao;
import ch.wellernet.hometv.util.dao.BaseInMemoryGenericDao;
import ch.wellernet.hometv.util.dao.IntegerIdGenerator;

@Component
public class PlayListItemInMemoryDaoImpl extends BaseInMemoryGenericDao<Integer, PlayListItem> implements
        PlayListItemDao {

    public PlayListItemInMemoryDaoImpl() {
        super(new IntegerIdGenerator<PlayListItem>());
    }

}
