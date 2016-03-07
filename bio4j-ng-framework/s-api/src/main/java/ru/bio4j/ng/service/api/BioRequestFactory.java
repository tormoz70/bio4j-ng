package ru.bio4j.ng.service.api;

import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.User;

import javax.servlet.http.HttpServletRequest;
import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public abstract class BioRequestFactory {
    private static final String QRY_PARAM_NAME_JSON_DATA = "jsonData";

    public BioRequest restore(HttpServletRequest request, final String moduleKey, final BioRoute route, final User usr) throws Exception {
        final String jsonDataAsQueryParam = request.getParameter(QRY_PARAM_NAME_JSON_DATA);
        StringBuilder jd = new StringBuilder();
        if(!isNullOrEmpty(jsonDataAsQueryParam))
            jd.append(jsonDataAsQueryParam);
        else
            Httpc.readDataFromRequest(request, jd);
        if(jd.length() == 0)
            jd.append("{}");
        BioRequest bioRequest;
        String bioRequestJson = jd.toString();
        try {
            Class<? extends BioRequest> clazz = route.getClazz();
            bioRequest = Jsons.decode(bioRequestJson, clazz);
        } catch (Exception e) {
            throw new Exception(String.format("Unexpected error while decoding BioRequest JSON: %s\n"+
                    " - Error: %s", bioRequestJson, e.getMessage()), e);
        }
        bioRequest.setModuleKey(moduleKey);
        bioRequest.setRequestType(route.getAlias());
        bioRequest.setUser(usr);
        return bioRequest;
    }

    public static class Ping extends BioRequestFactory {
    }

    public static class Logout extends BioRequestFactory {
    }

    public static class GetDataSet extends BioRequestFactory {
    }

    public static class GetRecord extends BioRequestFactory {
    }

    public static class DataSetPost extends BioRequestFactory {
    }

    public static class StoredProg extends BioRequestFactory {
    }

    public static class FormUpload extends BioRequestFactory {
        @Override
        public BioRequest restore(HttpServletRequest request, final String moduleKey, final BioRoute route, final User usr) throws Exception {
            return null;
        }

    }

}
