package ch.wellernet.hometv.master.api.service;

import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

import ch.wellernet.hometv.master.api.model.Channel;
import ch.wellernet.hometv.master.api.model.ChannelRestartMode;

public interface ChannelRessource {
    @Get
    public Channel load();

    @Post("?pause")
    public void pause();

    @Post("?restart")
    public void restart(ChannelRestartMode mode);

    @Post("?resume")
    public void resume();

    @Post("?start")
    public void start(List<Integer> playListItemIds);

    @Post("?stop")
    public void stop();
}
