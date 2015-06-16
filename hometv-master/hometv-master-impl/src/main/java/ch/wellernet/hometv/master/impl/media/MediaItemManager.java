/**
 *
 */
package ch.wellernet.hometv.master.impl.media;

import static java.lang.Long.parseLong;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Duration;
import org.springframework.stereotype.Component;

import ch.wellernet.hometv.master.api.model.PlayListItem;
import ch.wellernet.hometv.master.impl.dao.PlayListItemDao;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Component
public class MediaItemManager {

    private static final Log LOG = LogFactory.getLog(MediaItemManager.class);

    private static final String MEDIA_FILE_EXTENSION = ".avi";

    private int NEXT_PLAY_LIST_ITEM_ID = 1;

    @Resource
    private MediainfoProperties mediainfoProperties;

    @Resource
    private PlayListItemDao playListItemDao;

    /**
     * Scans a directory for media files (currently recognized by avi extension) and adds them to list of available play list items. If an item with
     * same local path is already present in list of available items, it will get updated with current attributes. Media files that are not valid (by
     * example if duration cannot be determined) are ignored or delete if already present in list of available items.
     *
     * If files passed is not a directory, nothing is done.
     *
     * @param directory
     *            the directory to scan
     * @param recursive
     *            <code>true</code> to scan recursively all subdirectories, <code>false</code> to limit search to given directory
     */
    public void scanDirectory(File directory, boolean recursive) {
        if (!directory.isDirectory()) {
            LOG.debug(format("Did nothing as %s is not a directory", directory));
            return;
        }
        Queue<File> stack = new LinkedList<File>(asList(directory.listFiles()));
        while (!stack.isEmpty()) {
            File file = stack.poll();
            if (!file.exists()) {
                LOG.debug(format("Ignored %s because it does not exist", file));
                continue;
            }
            if (file.isDirectory()) {
                if (recursive) {
                    LOG.debug(format("Recursively scanning subdirectory %s", file));
                    stack.addAll(asList(file.listFiles()));
                }
            } else if (file.getName().endsWith(MEDIA_FILE_EXTENSION)) {
                PlayListItem playListItem = playListItemDao.findByFile(file);
                if (playListItem == null) {
                    createNewPlayListItem(file);
                } else {
                    updateExistingPlayListItem(file, playListItem);
                }
            }
        }

    }

    /**
     * Build a title for a media item given ist file. Basicaly it takes the file name without the file extension.
     *
     * @param file
     *            the file of media item
     * @return a title identifying the item
     */
    String buildMediaItemTitle(File file) {
        String fileName = file.getName();
        return fileName.substring(0, fileName.length() - MEDIA_FILE_EXTENSION.length());
    }

    void createNewPlayListItem(File file) {
        PlayListItem playListItem;
        Duration duration = determineMediaDuration(file);
        if (duration == null) {
            LOG.debug(format("Ignored %s because its duration cannot be determined", file));
            return;
        }
        playListItem = new PlayListItem(NEXT_PLAY_LIST_ITEM_ID++);
        playListItem.setTitle(buildMediaItemTitle(file));
        playListItem.setFile(file);
        playListItem.setDuration(duration);
        playListItemDao.attach(playListItem);
        LOG.debug(format("Successfully added %s", file));
    }

    /**
     * Reads the duration of media item.
     *
     * TODO: refactor to external lib
     *
     * @param file
     *            the file of media item
     * @return the duration of media item or <code>null</code> it duration cannot be determined
     */
    Duration determineMediaDuration(File file) {
        Process process;
        try {
            process = getRuntime().exec(
                    format("\"%s\" --Output=\"Video;%%Duration%%\" %s", mediainfoProperties.getExecutable(), file.getAbsolutePath()));
            return process.waitFor() == 0 ? new Duration(parseLong(new BufferedReader(new InputStreamReader(process.getInputStream())).readLine()))
                    : null;
        } catch (InterruptedException | IOException exception) {
            LOG.warn("Caught exception", exception);
            return null;
        }
    }

    void updateExistingPlayListItem(File file, PlayListItem playListItem) {
        Duration duration = determineMediaDuration(file);
        if (duration == null) {
            playListItemDao.delete(playListItem.getId());
            LOG.debug(format("removed %s because its duration cannot be determined", file));
            return;
        }
        playListItem.setTitle(buildMediaItemTitle(file));
        playListItem.setDuration(duration);
        LOG.debug(format("Successfully updated %s", file));
    }
}
