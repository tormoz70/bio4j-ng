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
        protected boolean success;
        protected User user;
        protected List<BioError> exceptions;

        public T addError(BioError e) {
            if(exceptions == null)
                exceptions = new ArrayList<>();
            exceptions.add(e);
            return (T)this;
        }

        public T replaceError(BioError e, BioError newe) {
            if(exceptions != null && e != newe) {
                int indx = exceptions.indexOf(e);
                if(indx >= 0) {
                    if(newe != null)
                        exceptions.set(indx, newe);
                    else
                        exceptions.remove(indx);
                }
            }
            return (T)this;
        }

        public T success(boolean value) {
            success = value;
            return (T)this;
        }

        public T user(User value) {
            user = value;
            return (T)this;
        }

        public List<BioError> getExceptions() {
            return exceptions;
        }
        public boolean isSuccess() {
            return success;
        }
        public User getUser() {
            return user;
        }

        public BioResponse build() {
            BioResponse response = new BioResponse();
            response.setSuccess(success);
            response.setUser(user);
            response.setExceptions(this.exceptions);
            return response;
        }

    }

    public static abstract class Success <T extends Builder> extends Builder<T> {

    }

    public static class Login extends Success<Login> {

    }

    public static Login login() {
        return create(Login.class);
    }

    public static class AnError extends Builder<AnError> {

        @Override
        public BioResponse build() {
            BioResponse response = super.build();
            response.setSuccess(false);
            return response;
        }

    }
    public static AnError anError() {
        return create(AnError.class);
    }

    public static class Data extends Success<Data> {

        private String bioCode;
        private List<Param> bioParams;
        private RmtStatePack rmtStatePack;
        private StoreData packet;
        private Sort sort;
        private Expression filter;


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
            BioResponse response = super.build();
            response.setBioCode(bioCode);
            response.setBioParams(bioParams);
            response.setRmtStatePack(rmtStatePack);
            response.setPacket(packet);
            response.setSort(sort);
            response.setFilter(filter);
            return response;
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