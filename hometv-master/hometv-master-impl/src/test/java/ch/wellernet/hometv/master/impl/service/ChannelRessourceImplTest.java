package ch.wellernet.hometv.master.impl.service;

import static ch.wellernet.hometv.master.api.model.ChannelRestartMode.FROM_BEGIN_OF_PLAY_LIST;
import static ch.wellernet.hometv.master.api.model.ChannelState.IDLE;
import static ch.wellernet.hometv.master.api.model.ChannelState.PAUSED;
import static ch.wellernet.hometv.master.api.model.ChannelState.PLAYING;
import static ch.wellernet.hometv.master.api.model.ChannelState.STOPPED;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.ArrayUtils.subarray;
import static org.apache.commons.lang3.ArrayUtils.toObject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.wellernet.hometv.master.api.model.Channel;
import ch.wellernet.hometv.master.api.model.PlayListItem;
import ch.wellernet.hometv.master.impl.dao.ChannelDao;
import ch.wellernet.hometv.master.impl.dao.PlayListItemDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/application-context-test.xml")
public class ChannelRessourceImplTest {

    private static final int PLAY_LIST_ITEM_ID_1 = 101;
    private static final int PLAY_LIST_ITEM_ID_2 = 102;
    private static final int PLAY_LIST_ITEM_ID_3 = 103;

    private static final int CHANNEL_ID = 42;
    private static final int INEXISTENT_CHANNEL_ID = 66;

    // under test
    @Resource
    @InjectMocks
    private ChannelRessourceImpl channelRessource;

    @Mock
    private ChannelDao channelDao;

    @Mock
    private PlayListItemDao playListItemDao;

    private Channel channel;
    private PlayListItem[] playListItems;

    @Before
    public void setup() {
        initMocks(this);

        channel = new Channel(CHANNEL_ID);

        playListItems = new PlayListItem[] { new PlayListItem(PLAY_LIST_ITEM_ID_1),
                new PlayListItem(PLAY_LIST_ITEM_ID_2), new PlayListItem(PLAY_LIST_ITEM_ID_3) };

        when(channelDao.find(CHANNEL_ID)).thenReturn(channel);
        when(channelDao.find(INEXISTENT_CHANNEL_ID)).thenReturn(null);
        when(playListItemDao.find(Matchers.anyInt())).thenReturn(playListItems[0],
                subarray(playListItems, 1, playListItems.length));
    }

    @Test
    public void shouldChangeStateToPause() {
        // given
        channelRessource.setId(CHANNEL_ID);
        channel.setState(PLAYING);

        // when
        channelRessource.pause();

        // then
        assertThat(channel.getState(), is(PAUSED));
    }

    @Test
    public void shouldChangeStateToPlayingOnRestart() {
        // given
        channelRessource.setId(CHANNEL_ID);
        channel.setState(STOPPED);
        channel.getPlayList().addItem(playListItems[0]);
        channel.getPlayList().addItem(playListItems[1]);
        channel.setCurrentPlayListItem(playListItems[1]);

        // when
        channelRessource.restart(FROM_BEGIN_OF_PLAY_LIST);

        // then
        assertThat(channel.getState(), is(PLAYING));
        assertThat(channel.getCurrentPlayListItem(), is(playListItems[0]));
    }

    @Test
    public void shouldChangeStateToPlayingOnResume() {
        // given
        channelRessource.setId(CHANNEL_ID);
        channel.setState(PAUSED);

        // when
        channelRessource.resume();

        // then
        assertThat(channel.getState(), is(PLAYING));
    }

    @Test
    public void shouldChangeStateToPlayingOnStart() {
        // given
        channelRessource.setId(CHANNEL_ID);
        channel.setState(IDLE);
        int[] playListItemIds = { PLAY_LIST_ITEM_ID_1, PLAY_LIST_ITEM_ID_2, PLAY_LIST_ITEM_ID_3 };

        // when
        channelRessource.start(asList(toObject(playListItemIds)));

        // then
        assertThat(channel.getState(), is(PLAYING));
        assertThat(channel.getCurrentPlayListItem(), is(playListItems[0]));
        assertThat(channel.getPlayList().getItems(), is(asList(playListItems)));
    }

    @Test
    public void shouldChangeStateToStopped() {
        // given
        channelRessource.setId(CHANNEL_ID);
        channel.setState(PLAYING);

        // when
        channelRessource.stop();

        // then
        assertThat(channel.getState(), is(STOPPED));
    }

    @Test
    public void shouldFindChannel() {
        // given
        channelRessource.setId(CHANNEL_ID);

        // when
        Channel channel = channelRessource.load();

        // then
        assertThat(channel, equalTo(this.channel));
    }

    @Test
    public void shouldIgnorePauseWhenChannelIdle() {
        // given
        channelRessource.setId(CHANNEL_ID);
        channel.setState(IDLE);

        // when
        channelRessource.pause();

        // then
        assertThat(channel.getState(), is(IDLE));
    }

    @Test
    public void shouldIgnorePauseWhenChannelNotExists() {
        // given
        channelRessource.setId(INEXISTENT_CHANNEL_ID);
        channel.setState(PLAYING);

        // when
        channelRessource.pause();

        // then
        assertThat(channel.getState(), is(PLAYING));
    }

    @Test
    public void shouldIgnorePauseWhenChannelPaused() {
        // given
        channelRessource.setId(CHANNEL_ID);
        channel.setState(PAUSED);

        // when
        channelRessource.pause();

        // then
        assertThat(channel.getState(), is(PAUSED));
    }

    @Test
    public void shouldIgnoreRestartWhenChannelIdle() {
        // given
        channelRessource.setId(CHANNEL_ID);
        channel.setState(IDLE);

        // when
        channelRessource.restart(FROM_BEGIN_OF_PLAY_LIST);

        // then
        assertThat(channel.getState(), is(IDLE));
    }

    @Test
    public void shouldIgnoreRestartWhenChannelNotExists() {
        // given
        channelRessource.setId(INEXISTENT_CHANNEL_ID);
        channel.setState(STOPPED);

        // when
        channelRessource.restart(FROM_BEGIN_OF_PLAY_LIST);

        // then
        assertThat(channel.getState(), is(STOPPED));
    }

    @Test
    public void shouldIgnoreRestartWhenChannelPlaying() {
        // given
        channelRessource.setId(CHANNEL_ID);
        channel.setState(PLAYING);
        channel.getPlayList().addItem(playListItems[0]);
        channel.getPlayList().addItem(playListItems[1]);
        channel.setCurrentPlayListItem(playListItems[1]);

        // when
        channelRessource.restart(FROM_BEGIN_OF_PLAY_LIST);

        // then
        assertThat(channel.getState(), is(PLAYING));
        assertThat(channel.getCurrentPlayListItem(), is(playListItems[1]));
    }

    @Test
    public void shouldIgnoreResumeWhenChannelIdle() {
        // given
        channelRessource.setId(CHANNEL_ID);
        channel.setState(IDLE);

        // when
        channelRessource.resume();

        // then
        assertThat(channel.getState(), is(IDLE));
    }

    @Test
    public void shouldIgnoreResumeWhenChannelNotExists() {
        // given
        channelRessource.setId(INEXISTENT_CHANNEL_ID);
        channel.setState(PAUSED);

        // when
        channelRessource.resume();

        // then
        assertThat(channel.getState(), is(PAUSED));
    }

    @Test
    public void shouldIgnoreResumeWhenChannelPlaying() {
        // given
        channelRessource.setId(CHANNEL_ID);
        channel.setState(PLAYING);

        // when
        channelRessource.resume();

        // then
        assertThat(channel.getState(), is(PLAYING));
    }

    @Test
    public void shouldIgnoreStartWhenChannelNotExists() {
        // given
        channelRessource.setId(INEXISTENT_CHANNEL_ID);
        channel.setState(IDLE);
        int[] playListItemIds = { PLAY_LIST_ITEM_ID_1, PLAY_LIST_ITEM_ID_2, PLAY_LIST_ITEM_ID_3 };

        // when
        channelRessource.start(asList(toObject(playListItemIds)));

        // then
        assertThat(channel.getState(), is(IDLE));
    }

    @Test
    public void shouldIgnoreStartWhenChannelPlaying() {
        // given
        channelRessource.setId(CHANNEL_ID);
        channel.setState(PLAYING);
        int[] playListItemIds = { PLAY_LIST_ITEM_ID_1, PLAY_LIST_ITEM_ID_2, PLAY_LIST_ITEM_ID_3 };

        // when
        channelRessource.start(asList(toObject(playListItemIds)));

        // then
        assertThat(channel.getState(), is(PLAYING));
    }

    @Test
    public void shouldIgnoreStopWhenChannelIdle() {
        // given
        channelRessource.setId(CHANNEL_ID);
        channel.setState(IDLE);

        // when
        channelRessource.stop();

        // then
        assertThat(channel.getState(), is(IDLE));
    }

    @Test
    public void shouldIgnoreStopWhenChannelNotExists() {
        // given
        channelRessource.setId(INEXISTENT_CHANNEL_ID);
        channel.setState(PLAYING);

        // when
        channelRessource.stop();

        // then
        assertThat(channel.getState(), is(PLAYING));
    }

    @Test
    public void shouldIgnoreStopWhenChannelStopped() {
        // given
        channelRessource.setId(CHANNEL_ID);
        channel.setState(STOPPED);

        // when
        channelRessource.stop();

        // then
        assertThat(channel.getState(), is(STOPPED));
    }

    @Test
    public void shouldNotFindChannel() {
        // given
        channelRessource.setId(INEXISTENT_CHANNEL_ID);

        // when
        Channel channel = channelRessource.load();

        // then
        assertThat(channel, is(nullValue()));
    }
}
