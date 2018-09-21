package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.ErrorWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class ErrorWriterJsonImpl implements ErrorWriter {

    protected static ABean buildError(Exception exception) {
        ABean rslt = new ABean();
        rslt.put("success", false);
        rslt.put("exception", exception);
        return rslt;
    }

    @Override
    public void write(Exception exception, HttpServletResponse response, Boolean debugMode) throws Exception {
        ABean result = null;
        BioError error = BioError.wrap(exception);
        if(!debugMode) {
            if (error != null && !(error instanceof BioError.Login))
                result = buildError(new BioError("На сервере произошла непредвиденная ошибка!"));
            else
                result = buildError(error);
        } else
            result = buildError(error);
        PrintWriter writer = response.getWriter();
        writer.append(Jsons.encode(result));
    }
}
