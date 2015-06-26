package ch.wellernet.hometv.master.impl.dao;

import java.io.File;

import ch.wellernet.hibernate.dao.GenericDao;
import ch.wellernet.hometv.master.api.model.PlayListItem;

public interface PlayListItemDao extends GenericDao<Integer, PlayListItem> {

    /**
     * Retrieves a play list item by its local path.
     *
     * @param file
     *            the local path where item is stored
     * @return corresponding play list item
     */
    public PlayListItem findByFile(File file);
}
