/**
 *
 */
package ch.wellernet.hometv.master.impl.vlc;

import ch.wellernet.vlclib.VlcConnectionException;

/**
 * Exception that is thrown whenever a problem with communication on VLC media player is detected (see also {@link VlcConnectionException}).
 *
 * @author Lucien Weller <lucien@wellernet.ch>
 * @since 1.0.0
 */
public class ChannelVlcException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ChannelVlcException(String message) {
        super(message);
    }
}
