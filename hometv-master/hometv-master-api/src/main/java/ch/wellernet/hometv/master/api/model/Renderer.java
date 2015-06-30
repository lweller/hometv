/**
 *
 */
package ch.wellernet.hometv.master.api.model;

import static javax.persistence.CascadeType.DETACH;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import ch.wellernet.hibernate.model.IdentifyableObject;
import ch.wellernet.hibernate.model.ModelObjectBuilder;
import ch.wellernet.hometv.common.api.model.RendererInfo;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
@Entity
@Table(name = "RENDERER", schema = "HOMETV")
@SequenceGenerator(name = "primary_key", schema = "HOMETV", sequenceName = "SEQ_RENDERER")
public class Renderer extends IdentifyableObject<Integer> {
    public static class Builder extends ModelObjectBuilder<Integer, Renderer> {
        private Renderer instance;

        public Builder(RendererInfo rendererInfo) {
            instance = new Renderer();
            instance.info = rendererInfo;
        }

        @Override
        protected Renderer build() {
            return instance;
        }
    }

    private static final long serialVersionUID = 1L;

    @Embedded
    private RendererInfo info;

    @ManyToOne(cascade = DETACH)
    @JoinColumn(name = "ID_CHANNEL")
    private Channel channel;

    private Renderer() {
    }

    public Channel getChannel() {
        return channel;
    }

    public RendererInfo getInfo() {
        return info;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
