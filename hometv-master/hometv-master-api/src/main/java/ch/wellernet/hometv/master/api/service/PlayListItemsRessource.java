package ch.wellernet.hometv.master.api.service;

import org.restlet.resource.Get;

import ch.wellernet.hometv.master.api.model.PlayListItemList;

public interface PlayListItemsRessource {
    @Get
    public PlayListItemList load();
}
