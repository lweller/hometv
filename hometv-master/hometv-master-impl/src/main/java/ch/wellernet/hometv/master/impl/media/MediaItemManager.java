/**
 *
 */
package ch.wellernet.hometv.master.impl.media;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Duration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ch.wellernet.hometv.master.api.model.PlayListItem;
import ch.wellernet.hometv.master.impl.dao.PlayListItemDao;
import ch.wellernet.mediainfo.Mediainfo;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Component
@Transactional(propagation = REQUIRED)
public class MediaItemManager {

    private static final Log LOG = LogFactory.getLog(MediaItemManager.class);

    private static final String MEDIA_FILE_EXTENSION_AVI = ".avi";
    private static final String MEDIA_FILE_EXTENSION_M4V = ".m4v";

    @Resource
    private Mediainfo mediainfo;

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
            } else if (file.getName().endsWith(MEDIA_FILE_EXTENSION_AVI) || file.getName().endsWith(MEDIA_FILE_EXTENSION_M4V)) {
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
        return fileName.substring(0, fileName.length() - MEDIA_FILE_EXTENSION_AVI.length());
    }

    void createNewPlayListItem(File file) {
        Duration duration = mediainfo.determineVideoDuration(file);
        if (duration == null) {
            LOG.debug(format("Ignored %s because its duration cannot be determined", file));
            return;
        }
        new PlayListItem.Builder(buildMediaItemTitle(file), file, duration).build(playListItemDao);
        LOG.debug(format("Successfully added %s", file));
    }

    void updateExistingPlayListItem(File file, PlayListItem playListItem) {
        Duration duration = mediainfo.determineVideoDuration(file);
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
