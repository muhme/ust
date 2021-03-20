/*
 * ust - my VAT calculating project
 * AccountMaintenance.java - servlet to create, update or remove an account entry
 * hlu, Jan 30 2000 - Mar 20 2021
 * Tomcat 10
 */

package de.hlu.ust;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * This class is a servlet that maintains information about accounts.
 * 
 * @author Heiko Lübbe
 */
public class AccountMaintenanceS extends HttpServlet {

    /**
     * This method examines an HTTP request and retrieves some parameters from
     * the HTTP session first. Then it creates, removes or update an account. On
     * success it is setting the session attribute message (shown in green
     * later) and redirect to the Account.jsp with the right account id. In case
     * of an application error it is setting the session attribute error (shown
     * in red later) and redirects to the Account.jsp. In case of all other
     * exceptions no redirect is send. The servlet print the parameters in the
     * beginning and extend this with the stack trace.
     * 
     * @param req
     *            The HTTP Servlet request.
     * @param res
     *            The HTTP Servlet response.
     * @throws IOException
     *             From <code>getWriter</code> or <code>SendRedirect</code>.
     */
    public void service(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        int id = 0;

        // Must set the content type first
        res.setContentType("text/html");
        
        // Now we can obtain a PrintWriter
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession(true);

        // print all parameters
        out.println("parameters:<BR>");
        String param;
        for (Enumeration e = req.getParameterNames(); e.hasMoreElements();) {
            param = e.nextElement().toString();
            out.println(param + "=" + req.getParameter(param) + "<BR>");
        }

        try {
            String s;

            int number = 0;

            double vat = 16.0;
            String name = req.getParameter("name");
            String description = req.getParameter("description");
            String user = req.getParameter("user");
            int kind = Finance.OUT;
            boolean hasBankStatement = false;

            s = req.getParameter("number");
            if (s != null) {
                number = Integer.parseInt(s);
            }

            s = req.getParameter("id");
            if (s != null) {
                id = Integer.parseInt(s);
            }

            s = req.getParameter("vat");
            if (s != null) {
                vat = Double.parseDouble(s.replace(',', '.'));
            }

            s = req.getParameter("kind");
            if (s != null) {
                kind = Integer.parseInt(s);
            }

            if (req.getParameter("bankStatement").equals("true")) {
                hasBankStatement = true;
            }

            // UPDATE

            if (req.getParameter("update") != null) {
                Account account = Account.getAccountById(id);
                account.update(number, name, description, kind,
                        hasBankStatement, vat, user);
                session.setAttribute("message", "Das Konto " + name + " ("
                        + number + ") wurde geändert.");
                
                // using the configured user name?
                String configUser = Config.getUserName();
                if (!user.equalsIgnoreCase(configUser)) {
                    session.setAttribute("warning", "Als Benutzername ist \"" + configUser
                            + "\" konfiguriert, es wurde aber \"" + user + "\" verwendet!" );
                }

                // REMOVE

            } else if (req.getParameter("remove") != null) {

                boolean notInUse = true;

                // 1st check if the account id isn't used anymore
                Booking[] bookings = Booking.getAllBookings();
                for (int i = 0; i < bookings.length; ++i) {
                    if (bookings[i].getAccountId() == id) {
                        session.setAttribute("error", "Das Konto " + name + "("
                                + number + ") wird noch benutzt!");
                        notInUse = false;
                        break;
                    }
                }

                if (notInUse) {
                    Account account = new Account();
                    account.setId(id);
                    account.remove();
                    session.setAttribute("message", "Das Konto " + name + "("
                            + number + ") wurde gelöscht.");
                }

                // NEW

            } else if (req.getParameter("new") != null) {
                Account account = new Account();
                account.setVat(vat);
                account.setNumber(number);
                account.setName(name);
                account.setDescription(description);
                account.setKind(kind);
                account.setHasBankStatement(hasBankStatement);
                account.setDate(new GregorianCalendar());
                account.setUser(user);
                account.create();

                session.setAttribute("message", "Das Konto " + name + "("
                        + number + ") angelegt.");
            }
            String url = res.encodeRedirectURL("ListAccounts.jsp");
            res.sendRedirect(url);
        } catch (AppException ae) {
            String url = res.encodeRedirectURL("Account.jsp");
            session.setAttribute("error", ae.getMessage());
            url = res.encodeRedirectURL("Account.jsp?id=" + id);
            res.sendRedirect(url);
        } catch (Exception e) {
            out.println("Fehler: " + e + "<P>");
            e.printStackTrace(out);
        }
    }

    /** For serialization. */
    private static final long serialVersionUID = 1L;
}
