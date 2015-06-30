package ch.wellernet.hometv.master.api.model;

import java.util.List;

import org.restlet.util.WrapperList;

public class RendererList extends WrapperList<Renderer> {

    public RendererList() {
        super();
    }

    public RendererList(int initialCapacity) {
        super(initialCapacity);
    }

    public RendererList(List<Renderer> delegate) {
        super(delegate);
    }
}