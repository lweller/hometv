package ch.wellernet.hometv.master.impl.media;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;

import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import ch.wellernet.hometv.master.api.model.PlayListItem;
import ch.wellernet.hometv.master.impl.dao.PlayListItemDao;

public class MediaItemManagerTest {

    private static final String OTHER_MEDIA_TITLE = "wrongtitle";
    private static final Duration OTHER_MEDIA_ITEM_DURATION = new Duration(66000l);

    private static final String NOT_A_MEDIA_ITEM_FILE_NAME = "notafilm.txt";

    private static final String MEDIA_ITEM_TITLE = "film";
    private static final String MEDIA_ITEM_FILE_NAME = format("%s.avi", MEDIA_ITEM_TITLE);
    private static final Duration MEDIA_ITEM_DURATION = new Duration(42000l);

    // under test
    @Spy
    @InjectMocks
    private MediaItemManager mediaItemManager;

    @Mock
    private PlayListItemDao playListItemDao;

    @Before
    public void setup() {
        mediaItemManager = new MediaItemManager();
        initMocks(this);

        doReturn(MEDIA_ITEM_DURATION).when(mediaItemManager).determineMediaDuration(any(File.class));
    }

    @Test
    public void shouldCreateNewPlayListItem() {
        // given
        File file = buildFile(true, MEDIA_ITEM_FILE_NAME);
        doNothing().when(playListItemDao).attach(any(PlayListItem.class));
        doReturn(MEDIA_ITEM_DURATION).when(mediaItemManager).determineMediaDuration(file);

        // when
        mediaItemManager.createNewPlayListItem(file);

        // then
        ArgumentCaptor<PlayListItem> itemCaptor = forClass(PlayListItem.class);
        verify(playListItemDao).attach(itemCaptor.capture());
        PlayListItem newItem = itemCaptor.getValue();
        assertThat(newItem.getFile(), is(file));
        assertThat(newItem.getTitle(), is(MEDIA_ITEM_TITLE));
        assertThat(newItem.getDuration(), is(MEDIA_ITEM_DURATION));
    }

    @Test
    public void shouldCreateNewPlayListItemForFile() {
        // given
        File file = buildFile(true, MEDIA_ITEM_FILE_NAME);
        File directory = buildDirectory(true, file);
        doNothing().when(mediaItemManager).createNewPlayListItem(any(File.class));

        // when
        mediaItemManager.scanDirectory(directory, true);

        // then
        verify(mediaItemManager, times(1)).createNewPlayListItem(file);
    }

    @Test
    public void shouldCreateNewPlayListItemForFileInSubdirectoryWhenRecursing() {
        // given
        File file = buildFile(true, MEDIA_ITEM_FILE_NAME);
        File subDirectory = buildDirectory(true, file);
        File directory = buildDirectory(true, subDirectory);
        doNothing().when(mediaItemManager).createNewPlayListItem(any(File.class));

        // when
        mediaItemManager.scanDirectory(directory, true);

        // then
        verify(mediaItemManager, times(1)).createNewPlayListItem(file);
    }

    @Test
    public void shouldDeleteExisingPlayListItemWhenDurationCannotBeDetermined() {
        // given
        File file = buildFile(true, MEDIA_ITEM_FILE_NAME);
        PlayListItem playListItem = new PlayListItem(1);
        playListItem.setFile(file);
        playListItem.setTitle(OTHER_MEDIA_TITLE);
        playListItem.setDuration(OTHER_MEDIA_ITEM_DURATION);
        doReturn(null).when(mediaItemManager).determineMediaDuration(file);

        // when
        mediaItemManager.updateExistingPlayListItem(file, playListItem);

        // then
        verify(playListItemDao).delete(1);
    }

    @Test
    public void shouldIgnoreADirectoryThatDoesNotExist() {
        // given
        File file = buildDirectory(false);

        // when
        mediaItemManager.scanDirectory(file, true);

        // then
        verify(mediaItemManager, never()).createNewPlayListItem(file);
    }

    @Test
    public void shouldIgnoreAFileThatIsNotADirectory() {
        // given
        File file = buildFile(true, MEDIA_ITEM_FILE_NAME);

        // when
        mediaItemManager.scanDirectory(file, true);

        // then
        verify(mediaItemManager, never()).createNewPlayListItem(file);
    }

    @Test
    public void shouldIgnoreFileThatDoesNotExist() {
        // given
        File file = buildFile(false, NOT_A_MEDIA_ITEM_FILE_NAME);
        File directory = buildDirectory(true, file);

        // when
        mediaItemManager.scanDirectory(directory, true);

        // then
        verify(mediaItemManager, never()).createNewPlayListItem(file);
    }

    @Test
    public void shouldIgnoreFileThatDoesNotHasAviExtension() {
        // given
        File file = buildFile(true, NOT_A_MEDIA_ITEM_FILE_NAME);
        File directory = buildDirectory(true, file);

        // when
        mediaItemManager.scanDirectory(directory, true);

        // then
        verify(mediaItemManager, never()).createNewPlayListItem(file);
    }

    @Test
    public void shouldNotCreateNewPlayListItemForFileInSubdirectoryWhenNotRecursing() {
        // given
        File file = buildFile(true, MEDIA_ITEM_FILE_NAME);
        File subDirectory = buildDirectory(true, file);
        File directory = buildDirectory(true, subDirectory);

        // when
        mediaItemManager.scanDirectory(directory, false);

        // then
        verify(mediaItemManager, never()).createNewPlayListItem(file);
    }

    @Test
    public void shouldNotCreateNewPlayListItemWhenDurationCannotBeDetermined() {
        // given
        File file = buildFile(true, MEDIA_ITEM_FILE_NAME);
        doReturn(null).when(mediaItemManager).determineMediaDuration(file);

        // when
        mediaItemManager.createNewPlayListItem(file);

        // then
        verifyZeroInteractions(playListItemDao);
    }

    @Test
    public void shouldUpdateExisingPlayListItem() {
        // given
        File file = buildFile(true, MEDIA_ITEM_FILE_NAME);
        PlayListItem playListItem = new PlayListItem(1);
        playListItem.setFile(file);
        playListItem.setTitle(OTHER_MEDIA_TITLE);
        playListItem.setDuration(OTHER_MEDIA_ITEM_DURATION);
        doReturn(MEDIA_ITEM_DURATION).when(mediaItemManager).determineMediaDuration(file);

        // when
        mediaItemManager.updateExistingPlayListItem(file, playListItem);

        // then
        assertThat(playListItem.getFile(), is(file));
        assertThat(playListItem.getTitle(), is(MEDIA_ITEM_TITLE));
        assertThat(playListItem.getDuration(), is(MEDIA_ITEM_DURATION));
        verifyZeroInteractions(playListItemDao);
    }

    @Test
    public void shouldUpdteExistingPlayListItemForFile() {
        // given
        File file = buildFile(true, MEDIA_ITEM_FILE_NAME);
        PlayListItem playListItem = new PlayListItem(1);
        playListItem.setFile(file);
        doReturn(playListItem).when(playListItemDao).findByFile(file);
        File directory = buildDirectory(true, file);
        doNothing().when(mediaItemManager).updateExistingPlayListItem(any(File.class), any(PlayListItem.class));

        // when
        mediaItemManager.scanDirectory(directory, true);

        // then
        verify(mediaItemManager, never()).createNewPlayListItem(file);
        verify(mediaItemManager, times(1)).updateExistingPlayListItem(file, playListItem);
    }

    private File buildDirectory(boolean exists, File... files) {
        File directory = mock(File.class);
        doReturn(exists).when(directory).isDirectory();
        doReturn(false).when(directory).isFile();
        doReturn(exists).when(directory).exists();
        doReturn(exists ? files : null).when(directory).listFiles();
        return directory;
    }

    private File buildFile(boolean exists, String fileName) {
        File file = mock(File.class);
        doReturn(false).when(file).isDirectory();
        doReturn(exists).when(file).isFile();
        doReturn(exists).when(file).exists();
        doReturn(fileName).when(file).getName();
        return file;
    }
}
