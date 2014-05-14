package ru.bio4j.ng.client.extjs;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForwardServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //String destinationServer = "http://vps-nexus-bio4j.cloud.tilaa.com:9090/bio4j-spi/test/sproc";
        String qs = request.getQueryString();
        String destinationServer = "http://localhost:9090/biosrvapi"+((qs == null || qs.length() == 0) ? "" : "?"+qs);
        String destination = destinationServer; //request.getParameter("p");

        URL url = new URL(destination);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        try {


            StringBuffer jb = new StringBuffer();
            String line = null;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
            byte[] data = jb.toString().getBytes();

            connection.setRequestProperty("Content-Length", Integer.toString(data.length));
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(jb.toString());
            wr.flush();
            wr.close();
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                OutputStream out = response.getOutputStream();
                try {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1)
                        out.write(buffer, 0, bytesRead);
                    is.close();
                    out.close();
                } finally {
                    if (is != null)
                        is.close();
                    if (out != null)
                        out.close();
                }
            } else {
                response.getWriter().println(String.format("Error on forwarded server: [%d] - %s", connection.getResponseCode(), connection.getResponseMessage()));
            }
        } catch (Exception e) {
            response.getWriter().println(String.format("Error while forwarding. Error: %s", e.toString()));
        } finally {
            response.flushBuffer();
        }

    }
}
