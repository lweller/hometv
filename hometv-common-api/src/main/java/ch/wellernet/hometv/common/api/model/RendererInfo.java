/**
 *
 */
package ch.wellernet.hometv.common.api.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Embeddable
public class RendererInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "DEVICE_ID", unique = true)
    private String deviceId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "HOSTNAME")
    private String hostname;

    public RendererInfo(String deviceId, String name, String hostname) {
        this.deviceId = deviceId;
        this.name = name;
        this.hostname = hostname;
    }

    @SuppressWarnings("unused")
    private RendererInfo() {
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
        RendererInfo other = (RendererInfo) obj;
        if (deviceId == null) {
            if (other.deviceId != null) {
                return false;
            }
        } else if (!deviceId.equals(other.deviceId)) {
            return false;
        }
        return true;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getHostname() {
        return hostname;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (deviceId == null ? 0 : deviceId.hashCode());
        return result;
    }
}
