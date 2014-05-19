package ru.bio4j.ng.client.extjs;


import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.service.api.ConfigProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebServlet(name = "helloWorld", urlPatterns = {"/hello1"})
public class HelloServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private ConfigProvider configProvider;

    /**
     * Default constructor. 
     */
    public HelloServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(this.configProvider == null)
            this.configProvider = Utl.getService(this.getServletContext(), ConfigProvider.class);
		response.getWriter().print("HelloServlet! DriverName: "+configProvider.getConfig().getDriverName());
	}

}
