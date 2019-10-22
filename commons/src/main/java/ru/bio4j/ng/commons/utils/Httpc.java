package ru.bio4j.ng.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.Prop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

public class Httpc {
    private static final Logger LOG = LoggerFactory.getLogger(Httpc.class);

    public static interface Callback {
        void process(InputStream inputStream) throws Exception;
    }

    public static void readDataFromRequest(HttpServletRequest request, StringBuilder jd) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));) {
            String line;
            while ((line = reader.readLine()) != null)
                jd.append(line);
        } catch (IOException e) {
            throw BioError.wrap(e);
        }
    }

    private static String readJsonDataFromRequestParams(HttpServletRequest request, String jsonDataParam) {
        final String jsonDataAsQueryParam = request.getParameter(jsonDataParam);
        StringBuilder jd = new StringBuilder();
        if (!Strings.isNullOrEmpty(jsonDataAsQueryParam))
            jd.append(jsonDataAsQueryParam);
        if (jd.length() == 0)
            jd.append("{}");
        return jd.toString();
    }

    public static <T> T createBeanFromHttpRequest(HttpServletRequest request, Class<T> clazz) {
        if (request == null)
            throw new IllegalArgumentException("Argument \"request\" cannot be null!");
        if (clazz == null)
            throw new IllegalArgumentException("Argument \"bean\" cannot be null!");
        try {
            T result;
            result = (T) clazz.newInstance();
            for (java.lang.reflect.Field fld : Utl.getAllObjectFields(clazz)) {
                String fldName = fld.getName();
                Prop p = Utl.findAnnotation(Prop.class, fld);
                if (p != null) {
                    fldName = p.name();
                    String val = request.getParameter(fldName);
                    fld.setAccessible(true);
                    fld.set(result, val);
                }
            }
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            throw BioError.wrap(e);
        }
    }


    public static void forwardStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1)
            outputStream.write(buffer, 0, bytesRead);
    }

    public static void requestJson(String url, Callback callback) throws Exception {
        requestJson(url, null, callback);
    }

    public static final String CONTENT_TYPE_JSON = "application/json";

    public static void requestJson(String url, String jsonData, Callback callback) throws Exception {
        URL u = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", CONTENT_TYPE_JSON);
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        if(!Strings.isNullOrEmpty(jsonData)){
            byte[] data = jsonData.getBytes();
            connection.setRequestProperty("Content-Length", Integer.toString(data.length));
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(jsonData.toString());
            wr.flush();
            wr.close();
        }
        if (connection.getResponseCode() == 200) {
            InputStream is = connection.getInputStream();
            try {
                if(callback != null)
                    callback.process(is);
            } finally {
                if (is != null)
                    is.close();
            }
        } else {
            throw new Exception(String.format("Error on forwarded server: [%d] - %s", connection.getResponseCode(), connection.getResponseMessage()));
        }

    }

    public static void forwardRequestOld(String url, HttpServletRequest request, Callback callback) throws Exception {
        URL u = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setRequestMethod(request.getMethod());
        connection.setRequestProperty("Content-Type", request.getContentType());
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        InputStream inputStream = request.getInputStream();
        OutputStream outputStream = connection.getOutputStream();
        try {
            forwardStream(inputStream, outputStream);
        } finally {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
        }
        if (connection.getResponseCode() == 200) {
            InputStream is = connection.getInputStream();
            try {
                if(callback != null)
                    callback.process(is);
            } finally {
                if (is != null)
                    is.close();
            }
        } else {
            throw new Exception(String.format("Error on forwarded server: [%d] - %s", connection.getResponseCode(), connection.getResponseMessage()));
        }

    }

    public static void forwardRequestNew(String forwardUrl, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        final String method = req.getMethod();
        final boolean hasoutbody = (method.equals("POST"));

        final URL url = new URL(forwardUrl); //GlobalConstants.CLIENT_BACKEND_HTTPS  // no trailing slash
        //+ req.getRequestURI()
        //+ (req.getQueryString() != null ? "?" + req.getQueryString() : ""));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", req.getContentType());

        final Enumeration<String> headers = req.getHeaderNames();
        while (headers.hasMoreElements()) {
            final String header = headers.nextElement();
            final Enumeration<String> values = req.getHeaders(header);
            while (values.hasMoreElements()) {
                final String value = values.nextElement();
                conn.addRequestProperty(header, value);
            }
        }

        //conn.setFollowRedirects(false);  // throws AccessDenied exception
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(hasoutbody);
        conn.connect();

        final byte[] buffer = new byte[16384];
        while (hasoutbody) {
            final int read = req.getInputStream().read(buffer);
            if (read <= 0) break;
            conn.getOutputStream().write(buffer, 0, read);
        }

        resp.setStatus(conn.getResponseCode());
        for (int i = 0; ; ++i) {
            final String header = conn.getHeaderFieldKey(i);
            if (header == null) break;
            final String value = conn.getHeaderField(i);
            resp.setHeader(header, value);
        }

        while (true) {
            final int read = conn.getInputStream().read(buffer);
            if (read <= 0) break;
            resp.getOutputStream().write(buffer, 0, read);
        }
    }

    public static String getQueryString(HttpServletRequest request) {
        String queryString = null;
        for(String pn : request.getParameterMap().keySet())
            queryString = Strings.append(queryString, pn+"="+request.getParameterMap().get(pn)[0], "&");
        return queryString;
    }

    public static String extractFileNameFromPart(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length()-1);
            }
        }
        return "";
    }

    private static final String IP6LOCALHOSTADDR = "0:0:0:0:0:0:0:1";

    public static String extractRealRemoteAddr(HttpServletRequest request) {
        if(request != null) {
            String realRemoteAddr = request.getHeader("X-Real-IP");
            if(Strings.isNullOrEmpty(realRemoteAddr))
                realRemoteAddr = request.getHeader("X-Forwarded-For");
            String requestRemoteIP = request.getRemoteAddr();
            if(IP6LOCALHOSTADDR.equalsIgnoreCase(requestRemoteIP))
                requestRemoteIP = "127.0.0.1";
            return Strings.isNullOrEmpty(realRemoteAddr) ? requestRemoteIP : realRemoteAddr;
        }
        return null;
    }
    public static String extractRealRemoteClient(HttpServletRequest request) {
        if(request != null) {
            String userAgent = request.getHeader("User-Agent");
            String realRemoteClient = request.getParameter("clitp");
            if(Strings.isNullOrEmpty(realRemoteClient))
                realRemoteClient = request.getHeader("X-Real-Client");
            if(Strings.isNullOrEmpty(realRemoteClient))
                realRemoteClient = "Unknown";
            return String.format("X-Client: %s; User-Agent: %s", realRemoteClient, userAgent);
        }
        return null;
    }

}
