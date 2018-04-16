package ru.bio4j.ng.service.types;

import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.api.ErrorWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class ErrorWriterJsonImpl implements ErrorWriter {

    @Override
    public void write(Exception exception, HttpServletResponse response, Boolean debugMode) throws Exception {
        BioRespBuilder.AnErrorBuilder bresp = BioRespBuilder.anErrorBuilder().exception(BioError.wrap(exception));
        if(!debugMode) {
            BioError e = bresp.getException();
            if ((e != null) && !(e instanceof BioError.Login))
                bresp.exception(new BioError("На сервере произошла непредвиденная ошибка!"));
        }
        PrintWriter writer = response.getWriter();
        writer.append(bresp.json());
    }
}
