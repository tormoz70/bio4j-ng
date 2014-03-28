package org.example;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Servlet implementation class BioForwardServlet
 */
public class BioForwardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public BioForwardServlet() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String destinationServer = "http://vps-nexus-bio4j.cloud.tilaa.com:9090/bio4j-spi/test/status";
        String destination = destinationServer; //request.getParameter("p");
        //RequestDispatcher rd = getServletContext().getRequestDispatcher(destination);
        //rd.forward(request, response);

        URL url = new URL(destination);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        //connection.setRequestProperty("header1", "value1");
        try {
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
                response.getWriter().println(String.format("Error on forward server: [%d] - %s", connection.getResponseCode(), connection.getResponseMessage()));
            }
        } catch (Exception e) {
            response.getWriter().println(String.format("Error while forwarding. Error: %s", e.toString()));
        } finally {
            response.flushBuffer();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //String destinationServer = "http://vps-nexus-bio4j.cloud.tilaa.com:9090/bio4j-spi/test/sproc";
        String destinationServer = "http://localhost:9090/bio4j-spi/test/sproc";
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
                response.getWriter().println(String.format("Error on forward server: [%d] - %s", connection.getResponseCode(), connection.getResponseMessage()));
            }
        } catch (Exception e) {
            response.getWriter().println(String.format("Error while forwarding. Error: %s", e.toString()));
        } finally {
            response.flushBuffer();
        }

    }
}
