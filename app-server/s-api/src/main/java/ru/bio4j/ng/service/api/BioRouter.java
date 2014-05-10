package ru.bio4j.ng.service.api;

public interface BioRouter {
    public static interface Callback {
        public void run(String responseBody) throws Exception;
    }
    void route(String requestType, String requestBody, Callback callback) throws Exception;
}
