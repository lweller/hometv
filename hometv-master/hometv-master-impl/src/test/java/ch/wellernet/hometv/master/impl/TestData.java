package ch.wellernet.hometv.master.impl;

import static ch.wellernet.hometv.master.api.model.ChannelState.PLAYING;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.joda.time.Duration;
import org.springframework.stereotype.Component;

import ch.wellernet.hometv.master.api.model.Channel;
import ch.wellernet.hometv.master.api.model.PlayListItem;
import ch.wellernet.hometv.master.impl.dao.ChannelDao;
import ch.wellernet.hometv.master.impl.dao.PlayListItemDao;

@Component
public class TestData {

    @Resource
    private ChannelDao channelDao;

    @Resource
    private PlayListItemDao playListItemDao;

    @PostConstruct
    private void initTestData() {
        PlayListItem item = new PlayListItem(1);
        item.setTitle("Flim One");
        item.setDuration(new Duration(3845000));
        playListItemDao.save(item);

        item = new PlayListItem(2);
        item.setTitle("Flim Two");
        item.setDuration(new Duration(1845000));
        playListItemDao.save(item);

        Channel channel = new Channel(1);
        channelDao.save(channel);
        channel = new Channel(2);
        channel.setState(PLAYING);
        channel.setCurrentPlayListItem(item);
        channelDao.save(channel);
    }
}
