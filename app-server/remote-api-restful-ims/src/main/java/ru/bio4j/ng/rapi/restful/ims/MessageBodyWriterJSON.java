package ru.bio4j.ng.rapi.restful.ims;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
public class MessageBodyWriterJSON extends JacksonJsonProvider {

    @Override
    public ObjectMapper locateMapper(Class<?> type, MediaType mediaType)
    {
        ObjectMapper mapper = super.locateMapper(type, mediaType);
        //DateTime in ISO format "2012-04-07T17:00:00.000+0000" instead of 'long' format
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }

}
