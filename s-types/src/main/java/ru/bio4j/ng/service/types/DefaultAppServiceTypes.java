package ru.bio4j.ng.service.types;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.SQLActionScalar0;
import ru.bio4j.ng.database.api.SQLActionScalar1;
import ru.bio4j.ng.database.api.SQLActionVoid0;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;
import ru.bio4j.ng.service.api.AppService;
import ru.bio4j.ng.service.api.FCloudApi;
import ru.bio4j.ng.service.api.SQLDefinition;
import ru.bio4j.ng.service.api.SecurityService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static ru.bio4j.ng.service.types.ServletContextHolder.getServletContext;

public class DefaultAppServiceTypes implements AppServiceTypes {

    @Override
    public Class<? extends AppService> getAppServiceClass() {
        return AppService.class;
    }
    @Override
    public Class<? extends FCloudApi> getFCloudApiClass() {
        return FCloudApi.class;
    }
    @Override
    public Class<? extends SecurityService> getSecurityServiceClass() {
        return SecurityService.class;
    }

}
