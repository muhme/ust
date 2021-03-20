/*
 * ust - my VAT calculating project
 * BankAccountMaintenanceS.java - servlet to create, update or remove an bank account
 * hlu, 2003 - Mar 20 2021
 * Tomcat 10
 */

package de.hlu.ust;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * This class is a servlet that maintains information about bank accounts.
 * 
 * @author Heiko Lübbe
 */
public class BankAccountMaintenanceS extends HttpServlet {


    /**
     * This method examines an HTTP request, retrieves a bank account from the HTTP
     * session, updates the data and calls the corresponding bank account
     * maintenance function: insert or update. It then redirects the response to
     * the <tt>ListBankAccounts.jsp</tt> JSP page.
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

        String url = res.encodeRedirectURL("ListBankAccounts.jsp");

        // Must set the content type first
        res.setContentType("text/html");
        
        // Now we can obtain a PrintWriter
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession(true);

        // alle gesetzten Parameter ausgeben
        out.println("parameters:<BR>");
        String param;
        for (Enumeration e = req.getParameterNames(); e.hasMoreElements();) {
            param = e.nextElement().toString();
            out.println(param + "=" + req.getParameter(param) + "<BR>");
        }

        try {
            String s;

            int id = 0;
            String accountNumber = req.getParameter("accountNumber");
            String bankName = req.getParameter("bankName");
            String nickname = req.getParameter("nickname");
            String description = req.getParameter("description");
            String bankCode = req.getParameter("bankCode");
            String iban = req.getParameter("iban");
            String user = req.getParameter("user");

            s = req.getParameter("id");
            if (s != null) {
                id = Integer.parseInt(s);
            }

            // UPDATE

            if (req.getParameter("update") != null) {
                BankAccount bankAccount = BankAccount.getBankAccountById(id);
                bankAccount.update(accountNumber, bankName, nickname,
                        description, bankCode, iban, user);
                session.setAttribute("message", "Das Bankkonto \"" + nickname
                        + "\" wurde geändert.");
                
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
                BankStatement[] bankStatements = BankStatement
                        .getAllBankStatements();
                for (int i = 0; i < bankStatements.length; ++i) {
                    if (bankStatements[i].getBankAccountId() == id) {
                        session.setAttribute("error", "Das Bankkonto "
                                + nickname + " (" + accountNumber
                                + ") wird noch benutzt!");
                        notInUse = false;
                        break;
                    }
                }

                if (notInUse) {

                    BankAccount BankAccount = new BankAccount();
                    BankAccount.setId(id);
                    BankAccount.remove();
                    session.setAttribute("message", "Das Bankkonto \""
                            + nickname + "\" wurde gelöscht.");
                }
                // NEW

            } else if (req.getParameter("new") != null) {
                BankAccount BankAccount = new BankAccount();
                BankAccount.setAccountNumber(accountNumber);
                BankAccount.setBankName(bankName);
                BankAccount.setNickname(nickname);
                BankAccount.setDescription(description);
                BankAccount.setBankCode(bankCode);
                BankAccount.setIBAN(iban);
                BankAccount.setDate(new GregorianCalendar());
                BankAccount.setUser(user);
                BankAccount.create();

                session.setAttribute("message", "Das Bankkonto \"" + nickname
                        + "\" wurde angelegt.");
            }
            res.sendRedirect(url);
        } catch (AppException ae) {
            session.setAttribute("error", ae.getMessage());
            res.sendRedirect(url);
        } catch (Exception e) {
            out.println("Fehler: " + e + "<P>");
            e.printStackTrace(out);
        }
    }
    
    /** To fullfill the {@link Serializable} interface. */
    private static final long serialVersionUID = 1L;
}
