package ru.bio4j.ng.commons.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Httpc {
    public static interface Callback {
        void process(InputStream inputStream) throws Exception;
    }

    public static void readDataFromRequest(HttpServletRequest request, StringBuilder jd) throws IOException {
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null)
            jd.append(line);
    }

    public static void forwardStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1)
            outputStream.write(buffer, 0, bytesRead);
    }

    public static void requestJson(String url, String jsonData, Callback callback) throws Exception {
        URL u = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
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

    public static void forwardRequest(String url, HttpServletRequest request, Callback callback) throws Exception {
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

}