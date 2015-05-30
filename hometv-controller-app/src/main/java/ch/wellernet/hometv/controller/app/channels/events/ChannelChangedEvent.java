package ch.wellernet.hometv.controller.app.channels.events;

import ch.wellernet.hometv.master.api.model.Channel;

public class ChannelChangedEvent {
    private final Channel channel;

    public ChannelChangedEvent(Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChannelChangedEvent other = (ChannelChangedEvent) obj;
        if (channel == null) {
            if (other.channel != null) {
                return false;
            }
        } else if (!channel.equals(other.channel)) {
            return false;
        }
        return true;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (channel == null ? 0 : channel.hashCode());
        return result;
    }
}
