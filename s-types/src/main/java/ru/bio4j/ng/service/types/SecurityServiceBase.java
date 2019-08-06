package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.api.SecurityService;

public abstract class SecurityServiceBase extends AppServiceBase implements SecurityService {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityServiceBase.class);
}
