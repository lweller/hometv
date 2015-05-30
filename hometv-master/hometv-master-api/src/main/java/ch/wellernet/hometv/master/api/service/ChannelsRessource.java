package ch.wellernet.hometv.master.api.service;

import org.restlet.resource.Get;

import ch.wellernet.hometv.master.api.model.ChannelList;

public interface ChannelsRessource {
    @Get
    public ChannelList load();
}
