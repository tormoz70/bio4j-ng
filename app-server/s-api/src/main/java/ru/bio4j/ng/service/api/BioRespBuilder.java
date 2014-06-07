package ru.bio4j.ng.service.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.StoreData;
import ru.bio4j.ng.model.transport.jstore.filter.Expression;

import java.util.ArrayList;
import java.util.List;

public class BioRespBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(BioRespBuilder.class);

    private static <T extends Builder> T create(Class<T> type) {
        try {
            return type.newInstance();
        } catch (Exception e) {
            LOG.error("Unexpected error creating {} of {}!", type.getName(), BioRespBuilder.class.getName(), e);
            return null;
        }
    }

    public static abstract class Builder <T extends Builder> {
        protected List<BioError> exceptions;
        public abstract BioResponse build();

        public T addError(BioError e) {
            if(exceptions == null)
                exceptions = new ArrayList<>();
            exceptions.add(e);
            return (T)this;
        }


        public List<BioError> getExceptions() {
            return exceptions;
        }

    }

    public static class Login extends Builder<Login> {
        private boolean success;
        private User user;

        public Login success(boolean value) {
            success = value;
            return this;
        }

        public Login user(User value) {
            user = value;
            return this;
        }

        @Override
        public BioResponse build() {
            BioResponse response = new BioResponse();
            response.setSuccess(success);
            response.setUser(user);
            response.setExceptions(this.exceptions);
            return response;
        }

        public boolean isSuccess() {
            return success;
        }

        public User getUser() {
            return user;
        }
    }
    public static Login login() {
        return create(Login.class);
    }

    public static class AnError extends Builder<AnError> {

        @Override
        public BioResponse build() {
            BioResponse response = new BioResponse();
            response.setSuccess(false);
            response.setExceptions(this.exceptions);
            return response;
        }

    }
    public static AnError anError() {
        return create(AnError.class);
    }

    public static class Data extends Builder<Data> {

        private boolean success;
        private String bioCode;
        private List<Param> bioParams;
        private RmtStatePack rmtStatePack;
        private StoreData packet;
        private Sort sort;
        private Expression filter;


        public Data success(boolean value) {
            success = value;
            return this;
        }

        public Data bioCode(String value) {
            bioCode = value;
            return this;
        }

        public Data params(List<Param> value) {
            bioParams = value;
            return this;
        }

        public Data rmtStatePack(RmtStatePack value) {
            rmtStatePack = value;
            return this;
        }

        public Data packet(StoreData value) {
            packet = value;
            return this;
        }

        public Data sort(Sort value) {
            sort = value;
            return this;
        }
        public Data filter(Expression value) {
            filter = value;
            return this;
        }

        @Override
        public BioResponse build() {
            BioResponse response = new BioResponse();
            response.setSuccess(success);
            response.setExceptions(exceptions);
            response.setBioCode(bioCode);
            response.setBioParams(bioParams);
            response.setRmtStatePack(rmtStatePack);
            response.setPacket(packet);
            response.setSort(sort);
            response.setFilter(filter);
            return response;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getBioCode() {
            return bioCode;
        }

        public List<Param> getBioParams() {
            return bioParams;
        }

        public RmtStatePack getRmtStatePack() {
            return rmtStatePack;
        }

        public StoreData getPacket() {
            return packet;
        }

        public Sort getSort() {
            return sort;
        }

        public Expression getFilter() {
            return filter;
        }

    }
    public static Data data() {
        return create(Data.class);
    }

}
