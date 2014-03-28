package ru.bio4j.service.rs;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author  vladislavbocenin
 * @since 01.03.14.
 */
public class BioJerseyApplication extends ResourceConfig {
    public  BioJerseyApplication() {
        packages("ru.bio4j.service.rs");
        register(MessageBodyWriterJSON.class);
    }
}
