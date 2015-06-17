package ch.wellernet.hometv.master.impl.vlc;

import static ch.wellernet.hometv.master.api.model.ChannelRestartMode.FROM_BEGIN_OF_LAST_ITEM;
import static ch.wellernet.hometv.master.api.model.ChannelRestartMode.FROM_BEGIN_OF_PLAY_LIST;
import static ch.wellernet.hometv.master.api.model.ChannelRestartMode.FROM_LAST_POSITION;
import static ch.wellernet.hometv.master.api.model.ChannelState.IDLE;
import static ch.wellernet.hometv.master.api.model.ChannelState.PAUSED;
import static ch.wellernet.hometv.master.api.model.ChannelState.PLAYING;
import static ch.wellernet.hometv.master.api.model.ChannelState.STOPPED;
import static ch.wellernet.hometv.test.dao.DaoMockUtil.assignIdWhenAttached;
import static ch.wellernet.vlclib.MediaType.BROADCAST;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.ArrayUtils.subarray;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;
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
import ch.wellernet.hometv.test.model.PredefinedIdInitializer;
import ch.wellernet.vlclib.VlcConnectionException;
import ch.wellernet.vlclib.VlcInput;
import ch.wellernet.vlclib.VlcManager;
import ch.wellernet.vlclib.VlcMedia;
import ch.wellernet.vlclib.VlcOption;
import ch.wellernet.vlclib.VlcOutput;

public class ChannelVlcManagerTest {

    private static final int PLAY_LIST_ITEM_ID_1 = 101;
    private static final int PLAY_LIST_ITEM_ID_2 = 102;
    private static final int PLAY_LIST_ITEM_ID_3 = 103;

    private static final int CHANNEL_ID = 42;
    private static final int INEXISTENT_CHANNEL_ID = 666;

    private static final String MEDIA_NAME = format("channel%d", CHANNEL_ID);

    private static final Duration CURRENT_POSITION = new Duration(384238);

    private static final String PAUSE_MEDIA_ITEM_PATH = "/path/to/pause.avi";

    private static final VlcInput PAUSE_INPUT = new VlcInput(PAUSE_MEDIA_ITEM_PATH);

    private static final VlcOutput STANDARD_OUTPUT = new VlcOutput.Builder().module("gather").module("std").property("access", "http")
            .property("mux", "ps").property("dst", format(":8080/%s", MEDIA_NAME)).build();
    private static final VlcOption SOUT_KEEP_OPTION = new VlcOption("sout-keep");

    // under test
    @Spy
    @InjectMocks
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

        channel = new Channel.Builder().build(new PredefinedIdInitializer<Channel>(CHANNEL_ID));

        playListItems = new PlayListItem[] { createPlayListItem(PLAY_LIST_ITEM_ID_1), createPlayListItem(PLAY_LIST_ITEM_ID_2),
                createPlayListItem(PLAY_LIST_ITEM_ID_3) };

        when(channelDao.find(CHANNEL_ID)).thenReturn(channel);
        when(channelDao.find(INEXISTENT_CHANNEL_ID)).thenReturn(null);
        when(playListItemDao.find(Matchers.anyInt())).thenReturn(playListItems[0], subarray(playListItems, 1, playListItems.length));
    }

    @Test
    public void shouldCreateNewChannel() throws VlcConnectionException {
        // given
        assignIdWhenAttached(channelDao, CHANNEL_ID);

        // when
        Channel channel = channelVlcManager.createChannel();

        // then
        assertThat(channel, is(notNullValue()));

        verify(channelDao).attach(channel);
        verify(vlcManager).createMedia(new VlcMedia(format("channel%d", CHANNEL_ID), BROADCAST, true, STANDARD_OUTPUT, SOUT_KEEP_OPTION));
        verifyNoMoreInteractions(vlcManager, channelDao);
    }

    @Test
    public void shouldDoNothingWhenPausingAndChannelDoesNotExist() throws VlcConnectionException {
        // given
        channel.setState(PLAYING);
        channel.getPlayList().addItem(playListItems[0]);
        channel.getPlayList().addItem(playListItems[1]);
        channel.setCurrentPlayListItem(playListItems[1]);
        channel.setCurrentPosition(new Duration(0));
        channelVlcManager.setPauseMeidaItemPath(PAUSE_MEDIA_ITEM_PATH);

        // when
        channelVlcManager.pause(INEXISTENT_CHANNEL_ID);

        // then
        assertThat(channel.getState(), is(PLAYING));

        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldDoNothingWhenPausingInIdleState() throws VlcConnectionException {
        // given
        channel.setState(IDLE);

        // when
        channelVlcManager.pause(CHANNEL_ID);

        // then
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldDoNothingWhenPausingInPausededState() throws VlcConnectionException {
        // given
        channel.setState(PAUSED);

        // when
        channelVlcManager.pause(CHANNEL_ID);

        // then
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldDoNothingWhenPausingInStoppedState() throws VlcConnectionException {
        // given
        channel.setState(STOPPED);

        // when
        channelVlcManager.pause(CHANNEL_ID);

        // then
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldDoNothingWhenPlayingAndChannelDoesNotExist() throws VlcConnectionException {
        // given
        channel.setState(STOPPED);
        channel.getPlayList().addItem(playListItems[0]);
        channel.setCurrentPlayListItem(playListItems[1]);
        channel.setCurrentPosition(new Duration(0));

        // when
        channelVlcManager.play(INEXISTENT_CHANNEL_ID);

        // then
        assertThat(channel.getState(), is(STOPPED));
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldDoNothingWhenPlayingAndCurrentItemNotInPlayList() throws VlcConnectionException {
        // given
        channel.setState(STOPPED);
        channel.getPlayList().addItem(playListItems[0]);
        channel.setCurrentPlayListItem(playListItems[1]);
        channel.setCurrentPosition(new Duration(0));

        // when
        channelVlcManager.play(CHANNEL_ID);

        // then
        assertThat(channel.getState(), is(STOPPED));
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldDoNothingWhenRestartingAndChannelDoesNotExist() throws VlcConnectionException {
        // given
        channel.setState(STOPPED);
        doNothing().when(channelVlcManager).play(anyInt());

        // when
        channelVlcManager.restart(INEXISTENT_CHANNEL_ID, FROM_LAST_POSITION);

        // then
        assertThat(channel.getState(), is(STOPPED));
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldDoNothingWhenStopingAndChannelDoesNotExist() throws VlcConnectionException {
        // given
        channel.setState(PLAYING);

        // when
        channelVlcManager.stop(INEXISTENT_CHANNEL_ID);

        // then
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldDoNothingWhenStopingInStoppedState() throws VlcConnectionException {
        // given
        channel.setState(STOPPED);

        // when
        channelVlcManager.stop(CHANNEL_ID);

        // then
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldDoNothingWhenStoppingInIdleState() throws VlcConnectionException {
        // given
        channel.setState(IDLE);

        // when
        channelVlcManager.stop(CHANNEL_ID);

        // then
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldDoNotingWhenStartingAndChannelDoesNotExist() throws VlcConnectionException {
        // given
        List<Integer> playListItemsIds = asList(new Integer[] { PLAY_LIST_ITEM_ID_1, PLAY_LIST_ITEM_ID_2 });
        doNothing().when(channelVlcManager).updateVlcInput(any(Channel.class), anyListOf(Integer.class));
        doNothing().when(channelVlcManager).play(anyInt());

        // when
        channelVlcManager.start(INEXISTENT_CHANNEL_ID, playListItemsIds);

        // then
        assertThat(channel.getState(), is(IDLE));
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldLoadingExistingChannel() {
        // given
        // nothing special

        // when
        Channel channel = channelVlcManager.loadChannel(CHANNEL_ID);

        // then
        assertThat(channel, is(channel));
    }

    @Test
    public void shouldPauseAndPlayPauseItem() throws VlcConnectionException {
        // given
        channel.setState(PLAYING);
        channel.getPlayList().addItem(playListItems[0]);
        channel.getPlayList().addItem(playListItems[1]);
        channel.setCurrentPlayListItem(playListItems[1]);
        channel.setCurrentPosition(new Duration(0));
        channelVlcManager.setPauseMeidaItemPath(PAUSE_MEDIA_ITEM_PATH);

        // when
        channelVlcManager.pause(CHANNEL_ID);

        // then
        assertThat(channel.getState(), is(PAUSED));

        InOrder order = inOrder(vlcManager);
        order.verify(vlcManager).toggleLoopState(MEDIA_NAME);
        order.verify(vlcManager).addInputItem(MEDIA_NAME, PAUSE_INPUT);
        order.verify(vlcManager).play(MEDIA_NAME, 3);
        order.verify(vlcManager, times(2)).removeInputItem(MEDIA_NAME, 1);
        verifyNoMoreInteractions(vlcManager);
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
        channelVlcManager.play(CHANNEL_ID);

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
        channelVlcManager.play(CHANNEL_ID);

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

        doNothing().when(channelVlcManager).stop(anyInt());

        // when
        channelVlcManager.resetCurrentToBeginOfLastItem(channel);

        // then
        assertThat(channel.getCurrentPlayListItem(), is(playListItems[1]));
        assertThat(channel.getCurrentPosition(), is(new Duration(0)));

        verify(channelVlcManager).stop(CHANNEL_ID);
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

        doNothing().when(channelVlcManager).stop(anyInt());

        // when
        channelVlcManager.resetCurrentToBeginOfPlayList(channel);

        // then
        assertThat(channel.getCurrentPlayListItem(), is(playListItems[0]));
        assertThat(channel.getCurrentPosition(), is(new Duration(0)));

        verify(channelVlcManager).stop(CHANNEL_ID);
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldRestartAtLastPosition() throws VlcConnectionException {
        // given
        channel.setState(STOPPED);
        doNothing().when(channelVlcManager).play(anyInt());

        // when
        channelVlcManager.restart(CHANNEL_ID, FROM_LAST_POSITION);

        // then
        verify(channelVlcManager).play(CHANNEL_ID);
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldRestartFromBeginOfLastItem() throws VlcConnectionException {
        // given
        channel.setState(STOPPED);
        doNothing().when(channelVlcManager).resetCurrentToBeginOfLastItem(any(Channel.class));
        doNothing().when(channelVlcManager).play(anyInt());

        // when
        channelVlcManager.restart(CHANNEL_ID, FROM_BEGIN_OF_LAST_ITEM);

        // then
        verify(channelVlcManager).resetCurrentToBeginOfLastItem(channel);
        verify(channelVlcManager).play(CHANNEL_ID);
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldRestartFromBeginOfPlayList() throws VlcConnectionException {
        // given
        channel.setState(STOPPED);
        doNothing().when(channelVlcManager).resetCurrentToBeginOfPlayList(any(Channel.class));
        doNothing().when(channelVlcManager).play(anyInt());

        // when
        channelVlcManager.restart(CHANNEL_ID, FROM_BEGIN_OF_PLAY_LIST);

        // then
        verify(channelVlcManager).resetCurrentToBeginOfPlayList(channel);
        verify(channelVlcManager).play(CHANNEL_ID);
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldReturnNullWhenLoadingInexistentChannel() {
        // given
        // nothing special

        // when
        Channel channel = channelVlcManager.loadChannel(INEXISTENT_CHANNEL_ID);

        // then
        assertThat(channel, is(nullValue()));
    }

    @Test
    public void shouldStartPlayingAfterUpdatingPlayList() throws VlcConnectionException {
        // given
        List<Integer> playListItemsIds = asList(new Integer[] { PLAY_LIST_ITEM_ID_1, PLAY_LIST_ITEM_ID_2 });
        doNothing().when(channelVlcManager).updateVlcInput(any(Channel.class), anyListOf(Integer.class));
        doNothing().when(channelVlcManager).play(anyInt());

        // when
        channelVlcManager.start(CHANNEL_ID, playListItemsIds);

        // then
        InOrder order = inOrder(channelVlcManager);
        order.verify(channelVlcManager).stop(CHANNEL_ID);
        order.verify(channelVlcManager).updateVlcInput(channel, playListItemsIds);
        order.verify(channelVlcManager).play(CHANNEL_ID);
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldStopPlaying() throws VlcConnectionException {
        // given
        channel.setState(PLAYING);

        // when
        channelVlcManager.stop(CHANNEL_ID);

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
    public void shouldThrowExceptionWhenVlcAddPauseItemFailsWhilePausing() throws VlcConnectionException {
        // given
        channel.setState(PLAYING);
        doThrow(VlcConnectionException.class).when(vlcManager).addInputItem(anyString(), any(VlcInput.class));

        // when
        channelVlcManager.pause(CHANNEL_ID);

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
    public void shouldThrowExceptionWhenVlcPlayPauseItemFailsWhilePausing() throws VlcConnectionException {
        // given
        channel.setState(PLAYING);
        doThrow(VlcConnectionException.class).when(vlcManager).play(anyString(), anyInt());

        // when
        channelVlcManager.pause(CHANNEL_ID);

        // then
        // an exception should be thrown
    }

    @Test(expected = ChannelVlcException.class)
    public void shouldThrowExceptionWhenVlcRemovePauseItemFailsWhilePausing() throws VlcConnectionException {
        // given
        channel.setState(PLAYING);
        channel.getPlayList().addItem(playListItems[0]);
        channel.setCurrentPlayListItem(playListItems[0]);
        channel.setCurrentPosition(new Duration(0));
        doThrow(VlcConnectionException.class).when(vlcManager).removeInputItem(anyString(), anyInt());

        // when
        channelVlcManager.pause(CHANNEL_ID);

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
        channelVlcManager.play(CHANNEL_ID);

        // then
        // an exception should be thrown
    }

    @Test(expected = ChannelVlcException.class)
    public void shouldThrowExceptionWhenVlcStopFails() throws VlcConnectionException {
        // given
        channel.setState(PLAYING);
        doThrow(VlcConnectionException.class).when(vlcManager).stop(anyString());

        // when
        channelVlcManager.stop(CHANNEL_ID);

        // then
        // an exception should be thrown
    }

    @Test(expected = ChannelVlcException.class)
    public void shouldThrowExceptionWhenVlcToggleLoopFailsWhilePausing() throws VlcConnectionException {
        // given
        channel.setState(PLAYING);
        doThrow(VlcConnectionException.class).when(vlcManager).toggleLoopState(anyString());

        // when
        channelVlcManager.pause(CHANNEL_ID);

        // then
        // an exception should be thrown
    }

    @Test
    public void shouldUpdatePlayList() throws VlcConnectionException {
        // given
        channel.setState(ChannelState.STOPPED);
        channel.getPlayList().addItem(playListItems[1]);
        doNothing().when(channelVlcManager).stop(anyInt());
        doNothing().when(channelVlcManager).resetCurrentToBeginOfPlayList(any(Channel.class));

        // when
        channelVlcManager.updateVlcInput(channel, asList(new Integer[] { PLAY_LIST_ITEM_ID_1, PLAY_LIST_ITEM_ID_2, PLAY_LIST_ITEM_ID_3 }));

        // then
        assertThat(channel.getPlayList().getItems(), is(asList(playListItems)));

        InOrder order = inOrder(vlcManager, channelVlcManager);
        order.verify(vlcManager).clearInput(MEDIA_NAME);
        order.verify(vlcManager).addInputItem(MEDIA_NAME, new VlcInput(playListItems[0].getFile().getAbsolutePath()));
        order.verify(vlcManager).addInputItem(MEDIA_NAME, new VlcInput(playListItems[1].getFile().getAbsolutePath()));
        order.verify(vlcManager).addInputItem(MEDIA_NAME, new VlcInput(playListItems[2].getFile().getAbsolutePath()));
        order.verify(channelVlcManager).resetCurrentToBeginOfPlayList(channel);
        verifyNoMoreInteractions(vlcManager);
    }

    @Test
    public void shouldUpdatePlayListWithEmptyList() throws VlcConnectionException {
        // given
        channel.setState(ChannelState.STOPPED);
        channel.getPlayList().addItem(playListItems[1]);
        doNothing().when(channelVlcManager).stop(anyInt());

        // when
        channelVlcManager.updateVlcInput(channel, Collections.<Integer> emptyList());

        // then
        assertThat(channel.getPlayList().getItems(), is(Collections.<PlayListItem> emptyList()));

        InOrder order = inOrder(vlcManager, channelVlcManager);
        order.verify(vlcManager).clearInput(MEDIA_NAME);
        verifyNoMoreInteractions(vlcManager);
    }

    private PlayListItem createPlayListItem(int id) {
        PlayListItem playListItem = new PlayListItem.Builder(format("film%d", id), new File(format("/path/to/media/file%d.avi", id)), null)
                .build(new PredefinedIdInitializer<PlayListItem>(id));
        return playListItem;
    }
}
