package ru.bio4j.ng.service.types;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.LoginRec;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.ABean;

public class HttpSimpleClient {
    private HttpClient client = HttpClientBuilder.create().build();

    private static final int CONNECTION_TIMEOUT_MS = 300 * 1000; // 300 secs.
    public HttpResponse requestPost(String url, String loginOrSToken, String json, String forwardIP, String forwardClient) throws Exception {
        String body;
        String login = null;
        String stoken = null;
        if(!Strings.isNullOrEmpty(loginOrSToken)) {
            LoginRec loginRec = Utl.parsLogin(loginOrSToken);
            if (Strings.isNullOrEmpty(loginRec.getUsername()) || Strings.isNullOrEmpty(loginRec.getPassword()))
                stoken = loginOrSToken;
            else
                login = loginOrSToken;
        }
        HttpPost request = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
                .setConnectTimeout(CONNECTION_TIMEOUT_MS)
                .setSocketTimeout(CONNECTION_TIMEOUT_MS)
                .build();
        request.setConfig(requestConfig);
        request.setHeader("X-ForwardIP", forwardIP);
        request.setHeader("X-ForwardClient", forwardClient);
        request.setHeader("Content-Type", "application/json");
        if(!Strings.isNullOrEmpty(stoken))
            request.setHeader("X-SToken", stoken);

        if(!Strings.isNullOrEmpty(login)) {
            ABean bean = new ABean();
            bean.put("login", login);
            body = Jsons.encode(bean);
        } else {
            body = json;
        }
        if(!Strings.isNullOrEmpty(body)) {
            HttpEntity entity = new ByteArrayEntity(body.getBytes("UTF-8"));
            request.setEntity(entity);
        }
        return client.execute(request);
    }

    public HttpResponse requestGet(String url, String stoken, String forwardIP, String forwardClient) throws Exception {
        if (!Strings.isNullOrEmpty(stoken)) {
            HttpGet request = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
                    .setConnectTimeout(CONNECTION_TIMEOUT_MS)
                    .setSocketTimeout(CONNECTION_TIMEOUT_MS)
                    .build();
            request.setConfig(requestConfig);
            request.setHeader("X-ForwardIP", forwardIP);
            request.setHeader("X-ForwardClient", forwardClient);
            request.setHeader("X-SToken", stoken);
            HttpClient client = HttpClientBuilder.create().build();
            return client.execute(request);
        }
        return null;
    }



}
