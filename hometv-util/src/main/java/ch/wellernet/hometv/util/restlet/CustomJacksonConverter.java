package ch.wellernet.hometv.util.restlet;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;

import com.fasterxml.jackson.datatype.joda.JodaModule;

public class CustomJacksonConverter extends JacksonConverter {

    @Override
    protected <T> JacksonRepresentation<T> create(MediaType mediaType, T source) {
        JacksonRepresentation<T> representation = super.create(mediaType, source);
        representation.getObjectMapper().registerModule(new JodaModule());
        return representation;
    }

    @Override
    protected <T> JacksonRepresentation<T> create(Representation source, Class<T> objectClass) {
        JacksonRepresentation<T> representation = super.create(source, objectClass);
        representation.getObjectMapper().registerModule(new JodaModule());
        return representation;
    }

}
