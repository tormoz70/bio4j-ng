package ru.bio4j.ng.service.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  Created by ayrat on 08.05.14.
 */
public interface FCloudProvider extends BioService, BioConfigurable<FCloudConfig> {

    void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception;

}
