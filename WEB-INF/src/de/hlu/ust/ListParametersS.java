/*
 * ust - my VAT calculating project
 * ListParametersS.java - servlet to change some configuration values
 * hlu, Jan 30 2000 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
 */

package de.hlu.ust;

import javax.servlet.http.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import java.io.*;
import java.util.*;

/**
 * This class is a servlet that prints out the request parameteres and session
 * attributes.
 *
 * @author Heiko Lübbe
 */
public class ListParametersS extends HttpServlet {

    /**
     * This method examines an HTTP request, prints out the request parameters
     * and session attributes.
     * 
     * @param req
     *            The HTTP Servlet request.
     * @param res
     *            The HTTP Servlet response.
     * @throws IOException
     *             In case of I/O errors.
     */
    public void service(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        // Must set the content type first
        res.setContentType("text/html");

        // Now we can obtain a PrintWriter
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession(true);

        // print all request parameters
        out.println("request parameters:<BR>");
        String param;
        for (Enumeration e = req.getParameterNames(); e.hasMoreElements();) {
            param = e.nextElement().toString();
            out.println(param + "=" + req.getParameter(param) + "<BR>");
        }

        // show all session attribute
        out.println("session attributes:<BR>");
        String attr;
        for (Enumeration e = session.getAttributeNames(); e.hasMoreElements();) {
            attr = e.nextElement().toString();
            out.println(attr + "=" + session.getAttribute(attr) + "<BR>");
        }

        out.println("init parameters:<br>");

        if (initParameters != null) {
            out.println(initParameters);
        }

        out.println("context parameters:<br>");
        if (configParameters != null) {
            out.println(configParameters);
        }
    }

    /**
     * Init servelt method.
     * 
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig poConfig) throws ServletException {
        String param;

        // -- init base class
        super.init(poConfig);

        for (Enumeration e = poConfig.getInitParameterNames(); e
                .hasMoreElements();) {
            param = e.nextElement().toString();
            initParameters = param + "=" + poConfig.getInitParameter(param)
                    + "<br>";
        }

        ServletContext context = poConfig.getServletContext();

        for (Enumeration e = context.getAttributeNames(); e.hasMoreElements();) {
            param = e.nextElement().toString();
            configParameters = param + "=" + context.getAttribute(param)
                    + "<br>";
        }

    }

    /** Notice the init servlet parameters. */
    static String initParameters = null;

    /** Notice all config parameters. */
    static String configParameters = null;

    /** For serialisation. */
    private static final long serialVersionUID = 1L;
}
