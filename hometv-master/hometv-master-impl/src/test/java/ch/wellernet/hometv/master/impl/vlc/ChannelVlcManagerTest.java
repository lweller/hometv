package ch.wellernet.hometv.master.impl.vlc;

import static ch.wellernet.hometv.master.api.model.ChannelRestartMode.FROM_BEGIN_OF_LAST_ITEM;
import static ch.wellernet.hometv.master.api.model.ChannelRestartMode.FROM_BEGIN_OF_PLAY_LIST;
import static ch.wellernet.hometv.master.api.model.ChannelRestartMode.FROM_LAST_POSITION;
import static ch.wellernet.hometv.master.api.model.ChannelState.IDLE;
import static ch.wellernet.hometv.master.api.model.ChannelState.PLAYING;
import static ch.wellernet.hometv.master.api.model.ChannelState.STOPPED;
import static ch.wellernet.hometv.master.impl.vlc.ChannelVlcManager.getNextChannelId;
import static ch.wellernet.vlclib.MediaType.BROADCAST;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.ArrayUtils.subarray;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collections;
import java.util.List;

import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Spy;

import ch.wellernet.hometv.master.api.model.Channel;
import ch.wellernet.hometv.master.api.model.ChannelState;
import ch.wellernet.hometv.master.api.model.PlayListItem;
import ch.wellernet.hometv.master.impl.dao.ChannelDao;
import ch.wellernet.hometv.master.impl.dao.PlayListItemDao;
import ch.wellernet.vlclib.VlcConnectionException;
import ch.wellernet.vlclib.VlcInput;
import ch.wellernet.vlclib.VlcManager;
import ch.wellernet.vlclib.VlcMedia;
import ch.wellernet.vlclib.VlcOption;
import ch.wellernet.vlclib.VlcOutput;

public class ChannelVlcManagerTest {

    /**
     *
     */
    private static final int PLAY_LIST_ITEM_ID_1 = 101;
    private static final int PLAY_LIST_ITEM_ID_2 = 102;
    private static final int PLAY_LIST_ITEM_ID_3 = 103;

    private static final int CHANNEL_ID = 42;

    private static final String MEDIA_NAME = format("channel%d", CHANNEL_ID);

    private static final Duration CURRENT_POSITION = new Duration(384238);

    private static final VlcOutput STANDARD_OUTPUT = new VlcOutput.Builder().module("gather").module("std").property("access", "http")
            .property("mux", "ps").property("dst", format(":8080/%s", MEDIA_NAME)).build();
    private static final VlcOption SOUT_KEEP_OPTION = new VlcOption("sout-keep");

    // under test
    @InjectMocks
    @Spy
    private ChannelVlcManager channelVlcManager;

    @Mock
    private ChannelDao channelDao;

    @Mock
    private PlayListItemDao playListItemDao;

    @Mock
    private VlcManager vlcManager;

    private Channel channel;
    private PlayListItem[] playListItems;

    @Before
    public void setup() {
        channelVlcManager = new ChannelVlcManager();
        initMocks(this);

        channel = new Channel(CHANNEL_ID);

        playListItems = new PlayListItem[] { createPlayListItem(PLAY_LIST_ITEM_ID_1), createPlayListItem(PLAY_LIST_ITEM_ID_2),
                createPlayListItem(PLAY_LIST_ITEM_ID_3) };

        when(playListItemDao.find(Matchers.anyInt())).thenReturn(playListItems[0], subarray(playListItems, 1, playListItems.length));
    }

    @Test
    public void shouldCreateNewChannel() throws VlcConnectionException {
        // given
        int id = getNextChannelId();

        // when
        Channel channel = channelVlcManager.createChannel();

        // then
        assertThat(channel, is(notNullValue()));

        verify(channelDao).save(channel);
        verify(vlcManager).createMedia(new VlcMedia(format("channel%d", id), BROADCAST, true, STANDARD_OUTPUT, SOUT_KEEP_OPTION));
        verifyNoMoreInteractions(vlcManager, channelDao);
    }

    @Test
    public void shouldDoNothingWhenCurrentItemNotInPlayList() throws VlcConnectionException {
        // given
        channel.setState(STOPPED);
        channel.getPlayList().addItem(playListItems[0]);
        channel.setCurrentPlayListItem(playListItems[1]);
        channel.setCurrentPosition(new Duration(0));

        // when
        channelVlcManager.play(channel);

        // then
        assertThat(channel.getState(), is(STOPPED));
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldDoNothingWhenStopingInIdleState() throws VlcConnectionException {
        // given
        channel.setState(IDLE);

        // when
        channelVlcManager.stop(channel);

        // then
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldDoNothingWhenStopingInStoppedState() throws VlcConnectionException {
        // given
        channel.setState(STOPPED);

        // when
        channelVlcManager.stop(channel);

        // then
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldLoadingExistingChannel() {
        // given
        when(channelDao.find(CHANNEL_ID)).thenReturn(channel);

        // when
        Channel channel = channelVlcManager.loadChannel(CHANNEL_ID);

        // then
        assertThat(channel, is(channel));
    }

    @Test
    public void shouldPlayCurrentItemAtCurrentPosition() throws VlcConnectionException {
        // given
        channel.setState(STOPPED);
        channel.getPlayList().addItem(playListItems[0]);
        channel.getPlayList().addItem(playListItems[1]);
        channel.setCurrentPlayListItem(playListItems[1]);
        channel.setCurrentPosition(new Duration(42));

        // when
        channelVlcManager.play(channel);

        // then
        assertThat(channel.getState(), is(PLAYING));

        InOrder order = inOrder(vlcManager);
        order.verify(vlcManager).play(MEDIA_NAME, 2);
        order.verify(vlcManager).seek(MEDIA_NAME, new Duration(42));
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldPlayCurrentItemFromBegin() throws VlcConnectionException {
        // given
        channel.setState(STOPPED);
        channel.getPlayList().addItem(playListItems[0]);
        channel.getPlayList().addItem(playListItems[1]);
        channel.setCurrentPlayListItem(playListItems[1]);
        channel.setCurrentPosition(new Duration(0));

        // when
        channelVlcManager.play(channel);

        // then
        assertThat(channel.getState(), is(PLAYING));

        verify(vlcManager).play(MEDIA_NAME, 2);
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldResetToBeginOfLastItem() {
        // given
        channel.setState(PLAYING);
        channel.getPlayList().addItem(playListItems[0]);
        channel.getPlayList().addItem(playListItems[1]);
        channel.setCurrentPlayListItem(playListItems[1]);
        channel.setCurrentPosition(CURRENT_POSITION);

        doNothing().when(channelVlcManager).stop(any(Channel.class));

        // when
        channelVlcManager.resetCurrentToBeginOfLastItem(channel);

        // then
        assertThat(channel.getCurrentPlayListItem(), is(playListItems[1]));
        assertThat(channel.getCurrentPosition(), is(new Duration(0)));

        verify(channelVlcManager).stop(channel);
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldResetToBeginOfPlayList() {
        // given
        channel.setState(PLAYING);
        channel.getPlayList().addItem(playListItems[0]);
        channel.getPlayList().addItem(playListItems[1]);
        channel.setCurrentPlayListItem(playListItems[1]);
        channel.setCurrentPosition(new Duration(384238));

        doNothing().when(channelVlcManager).stop(any(Channel.class));

        // when
        channelVlcManager.resetCurrentToBeginOfPlayList(channel);

        // then
        assertThat(channel.getCurrentPlayListItem(), is(playListItems[0]));
        assertThat(channel.getCurrentPosition(), is(new Duration(0)));

        verify(channelVlcManager).stop(channel);
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldRestartAtLastPosition() throws VlcConnectionException {
        // given
        doNothing().when(channelVlcManager).play(any(Channel.class));

        // when
        channelVlcManager.restart(channel, FROM_LAST_POSITION);

        // then
        verify(channelVlcManager).play(channel);
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldRestartFromBeginOfLastItem() throws VlcConnectionException {
        // given
        doNothing().when(channelVlcManager).resetCurrentToBeginOfLastItem(any(Channel.class));
        doNothing().when(channelVlcManager).play(any(Channel.class));

        // when
        channelVlcManager.restart(channel, FROM_BEGIN_OF_LAST_ITEM);

        // then
        verify(channelVlcManager).resetCurrentToBeginOfLastItem(channel);
        verify(channelVlcManager).play(channel);
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldRestartFromBeginOfPlayList() throws VlcConnectionException {
        // given
        doNothing().when(channelVlcManager).resetCurrentToBeginOfPlayList(any(Channel.class));
        doNothing().when(channelVlcManager).play(any(Channel.class));

        // when
        channelVlcManager.restart(channel, FROM_BEGIN_OF_PLAY_LIST);

        // then
        verify(channelVlcManager).resetCurrentToBeginOfPlayList(channel);
        verify(channelVlcManager).play(channel);
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldReturnNullWhenLoadingInexistentChannel() {
        // given
        when(channelDao.find(CHANNEL_ID)).thenReturn(null);

        // when
        Channel channel = channelVlcManager.loadChannel(CHANNEL_ID);

        // then
        assertThat(channel, is(nullValue()));
    }

    @Test
    public void shouldStartPlayinfAfterUpdatingPlayList() throws VlcConnectionException {
        // given
        List<Integer> playListItemsIds = asList(new Integer[] { PLAY_LIST_ITEM_ID_1, PLAY_LIST_ITEM_ID_2 });
        doNothing().when(channelVlcManager).updateVlcInput(any(Channel.class), anyListOf(Integer.class));
        doNothing().when(channelVlcManager).play(any(Channel.class));

        // when
        channelVlcManager.start(channel, playListItemsIds);

        // then
        verify(channelVlcManager).updateVlcInput(channel, playListItemsIds);
        verify(channelVlcManager).play(channel);
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldStopPlaying() throws VlcConnectionException {
        // given
        channel.setState(PLAYING);

        // when
        channelVlcManager.stop(channel);

        // then
        verify(vlcManager).stop(MEDIA_NAME);
        verifyNoMoreInteractions(vlcManager);
    }

    @Test(expected = ChannelVlcException.class)
    public void shouldThrowExceptionWhenVlcAddInputFails() throws VlcConnectionException {
        // given
        channel.setState(PLAYING);
        doThrow(VlcConnectionException.class).when(vlcManager).addInputItem(anyString(), any(VlcInput.class));

        // when
        channelVlcManager.updateVlcInput(channel, asList(new Integer[] { PLAY_LIST_ITEM_ID_1, PLAY_LIST_ITEM_ID_2, PLAY_LIST_ITEM_ID_3 }));

        // then
        // an exception should be thrown
    }

    @Test(expected = ChannelVlcException.class)
    public void shouldThrowExceptionWhenVlcClearInputFails() throws VlcConnectionException {
        // given
        channel.setState(PLAYING);
        doThrow(VlcConnectionException.class).when(vlcManager).clearInput(anyString());

        // when
        channelVlcManager.updateVlcInput(channel, Collections.<Integer> emptyList());

        // then
        // an exception should be thrown
    }

    @Test(expected = ChannelVlcException.class)
    public void shouldThrowExceptionWhenVlcMediaCreationFails() throws VlcConnectionException {
        // given
        doThrow(VlcConnectionException.class).when(vlcManager).createMedia(any(VlcMedia.class));

        // when
        channelVlcManager.createChannel();

        // then
        // an exception should be thrown
    }

    @Test(expected = ChannelVlcException.class)
    public void shouldThrowExceptionWhenVlcSeekFails() throws VlcConnectionException {
        // given
        channel.setState(STOPPED);
        channel.getPlayList().addItem(playListItems[0]);
        channel.setCurrentPlayListItem(playListItems[0]);
        channel.setCurrentPosition(new Duration(42));
        doThrow(VlcConnectionException.class).when(vlcManager).seek(anyString(), any(Duration.class));

        // when
        channelVlcManager.play(channel);

        // then
        // an exception should be thrown
    }

    @Test(expected = ChannelVlcException.class)
    public void shouldThrowExceptionWhenVlcStopFails() throws VlcConnectionException {
        // given
        channel.setState(PLAYING);
        doThrow(VlcConnectionException.class).when(vlcManager).stop(anyString());

        // when
        channelVlcManager.stop(channel);

        // then
        // an exception should be thrown
    }

    @Test
    public void shouldUpdatePlayList() throws VlcConnectionException {
        // given
        channel.setState(ChannelState.STOPPED);
        channel.getPlayList().addItem(playListItems[1]);
        doNothing().when(channelVlcManager).stop(any(Channel.class));
        doNothing().when(channelVlcManager).resetCurrentToBeginOfPlayList(any(Channel.class));

        // when
        channelVlcManager.updateVlcInput(channel, asList(new Integer[] { PLAY_LIST_ITEM_ID_1, PLAY_LIST_ITEM_ID_2, PLAY_LIST_ITEM_ID_3 }));

        // then
        assertThat(channel.getPlayList().getItems(), is(asList(playListItems)));

        InOrder order = inOrder(vlcManager, channelVlcManager);
        order.verify(channelVlcManager).stop(channel);
        order.verify(vlcManager).clearInput(MEDIA_NAME);
        order.verify(vlcManager).addInputItem(MEDIA_NAME, new VlcInput(playListItems[0].getLocalPath()));
        order.verify(vlcManager).addInputItem(MEDIA_NAME, new VlcInput(playListItems[1].getLocalPath()));
        order.verify(vlcManager).addInputItem(MEDIA_NAME, new VlcInput(playListItems[2].getLocalPath()));
        order.verify(channelVlcManager).resetCurrentToBeginOfPlayList(channel);
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldUpdatePlayListWithEmptyList() throws VlcConnectionException {
        // given
        channel.setState(ChannelState.STOPPED);
        channel.getPlayList().addItem(playListItems[1]);
        doNothing().when(channelVlcManager).stop(any(Channel.class));

        // when
        channelVlcManager.updateVlcInput(channel, Collections.<Integer> emptyList());

        // then
        assertThat(channel.getPlayList().getItems(), is(Collections.<PlayListItem> emptyList()));

        InOrder order = inOrder(vlcManager, channelVlcManager);
        order.verify(channelVlcManager).stop(channel);
        order.verify(vlcManager).clearInput(MEDIA_NAME);
        verifyNoMoreInteractions(vlcManager);
    }

    private PlayListItem createPlayListItem(int id) {
        PlayListItem playListItem = new PlayListItem(id);
        playListItem.setLocalPath(format("/path/to/media/file%d.avi", id));
        return playListItem;
    }
}