package ru.bio4j.ng.service.api;

import java.io.InputStream;

/**
 *  Created by ayrat on 08.05.14.
 */
public interface FCloudProvider extends BioService {
    BioFCloudApiModule getApi() throws Exception;
}
