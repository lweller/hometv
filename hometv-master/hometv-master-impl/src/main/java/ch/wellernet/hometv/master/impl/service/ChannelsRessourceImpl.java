package ch.wellernet.hometv.master.impl.service;

import javax.annotation.Resource;

import org.restlet.resource.ServerResource;

import ch.wellernet.hometv.master.api.model.ChannelList;
import ch.wellernet.hometv.master.api.service.ChannelsRessource;
import ch.wellernet.hometv.master.impl.dao.ChannelDao;
import ch.wellernet.restlet.spring.Restlet;

@Restlet(router = "root", uriTemplate = "/channels")
public class ChannelsRessourceImpl extends ServerResource implements ChannelsRessource {

    @Resource
    private ChannelDao channelDao;

    /**
     * @see ch.wellernet.hometv.master.api.service.ChannelsRessource#load()
     */
    @Override
    public ChannelList load() {
        return new ChannelList(channelDao.findAll());
    }

}
