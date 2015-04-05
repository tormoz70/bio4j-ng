package ru.bio4j.ng.rapi.restful.ims;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author  vladislavbocenin
 * @since 01.03.14.
 */
public class BioJerseyApplication extends ResourceConfig {
    public  BioJerseyApplication() {
        packages("ru.bio4j.ng.rapi.restful.ims");
        register(MessageBodyWriterJSON.class);
    }
}
