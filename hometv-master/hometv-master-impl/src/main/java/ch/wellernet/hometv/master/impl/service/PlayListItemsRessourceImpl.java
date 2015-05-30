package ch.wellernet.hometv.master.impl.service;

import javax.annotation.Resource;

import org.restlet.resource.ServerResource;

import ch.wellernet.hometv.master.api.model.PlayListItemList;
import ch.wellernet.hometv.master.api.service.PlayListItemsRessource;
import ch.wellernet.hometv.master.impl.dao.PlayListItemDao;
import ch.wellernet.hometv.util.restlet.Restlet;

@Restlet(router = "root", uriTemplate = "/playlistitems")
public class PlayListItemsRessourceImpl extends ServerResource implements PlayListItemsRessource {

    @Resource
    private PlayListItemDao playListItemDao;

    /**
     * @see ch.wellernet.hometv.master.api.service.PlayListItemsRessource#load()
     */
    @Override
    public PlayListItemList load() {
        return new PlayListItemList(playListItemDao.findAll());
    }

}
