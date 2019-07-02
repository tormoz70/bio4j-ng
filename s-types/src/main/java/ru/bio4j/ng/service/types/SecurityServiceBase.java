package ru.bio4j.ng.service.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.StoredProgMetadata;
import ru.bio4j.ng.model.transport.AnConfig;
import ru.bio4j.ng.service.api.HttpParamMap;
import ru.bio4j.ng.service.api.LoginProcessor;
import ru.bio4j.ng.service.api.SecurityService;
import ru.bio4j.ng.service.api.UpdelexSQLDef;

public abstract class SecurityServiceBase extends AppServiceBase implements SecurityService {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityServiceBase.class);
}
