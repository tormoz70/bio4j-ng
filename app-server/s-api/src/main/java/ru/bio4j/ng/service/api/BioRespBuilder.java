package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.BioResponse;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.RmtStatePack;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.StoreData;
import ru.bio4j.ng.model.transport.jstore.filter.Expression;

import java.util.ArrayList;
import java.util.List;

public class BioRespBuilder {

    public static <T extends Builder> T create(Class<T> type) throws Exception {
        return type.newInstance();
    }

    public static abstract class Builder {
        public abstract BioResponse build();
    }

    public static class Login extends Builder {
        private boolean success;
        private User user;
        private List<Exception> exceptions;

        public Login success(boolean value) {
            success = value;
            return this;
        }

        public Login user(User value) {
            user = value;
            return this;
        }

        public Login addError(Exception e) {
            if(exceptions == null)
                exceptions = new ArrayList<>();
            exceptions.add(e);
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
        public List<Exception> getExceptions() {
            return exceptions;
        }
    }

    public static class AnError extends Builder {
        private List<Exception> exceptions;

        public AnError addError(Exception e) {
            if(exceptions == null)
                exceptions = new ArrayList<>();
            exceptions.add(e);
            return this;
        }

        @Override
        public BioResponse build() {
            BioResponse response = new BioResponse();
            response.setSuccess(false);
            response.setExceptions(this.exceptions);
            return response;
        }

        public List<Exception> getExceptions() {
            return exceptions;
        }
    }

    public static class Data extends Builder {

        private boolean success;
        private List<Exception> exceptions;
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

        public Data addError(Exception e) {
            if(exceptions == null)
                exceptions = new ArrayList<>();
            exceptions.add(e);
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

        public List<Exception> getExceptions() {
            return exceptions;
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

}
