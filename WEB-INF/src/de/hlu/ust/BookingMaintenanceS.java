/*
 * ust - my VAT calculating project
 * BookingMaintenanceS.java - servlet to create, update or remove a booking
 * hlu, May 15 2001 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
 */

package de.hlu.ust;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * This class is a servlet that maintains bookings.
 * 
 * @author Heiko Lübbe
 */
public class BookingMaintenanceS extends HttpServlet {

    /**
     * This method examines an HTTP request and retrieves some parameters from
     * the HTTP session first. Then it creates, removes or update an booking
     * entry. On success it is setting the session attribute message (shown in
     * green later) and redirect to the Booking.jsp with the right booking
     * identifier. In case of an application error it is setting the session
     * attribute error (shown in red later) and redirects to the Booking.jsp. In
     * case of all other exceptions no redirect is send. The servlet print the
     * parameters in the beginning and extend this with the stack trace.
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
        String url = res.encodeRedirectURL("Booking.jsp");
        String s;
        String gross = req.getParameter("gross");
        String description = req.getParameter("description");
        int bankStatementId = 0;
        int accountId = 0;
        String bookingDate = req.getParameter("bookingDate");
        int id = 0;
        String user = req.getParameter("user");
        double vat = 16.0;
        int bankAccountId = 0;
        int number = 0;
        int page = 0;
        int position = 0;
        // Must set the content type first
        res.setContentType("text/html");
        
        // Now we can obtain a PrintWriter
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession(true);
        // show all set parameters (only shown in error case)
        out.println("parameters:<BR>");
        String param;
        for (Enumeration e = req.getParameterNames(); e.hasMoreElements();) {
            param = e.nextElement().toString();
            out.println(param + "=" + req.getParameter(param) + "<BR>");
        }
        try {
            if ((s = req.getParameter("accountId")) != null) {
                accountId = Integer.parseInt(s);
            }
            if ((s = req.getParameter("id")) != null) {
                id = Integer.parseInt(s);
            }
            if (((s = req.getParameter("vat")) != null) && (s.length() > 0)) {
                vat = Double.parseDouble(s.replace(',', '.'));
            }
            if ((s = req.getParameter("bankAccountId")) != null) {
                bankAccountId = Integer.parseInt(s);
            }
            if (((s = req.getParameter("number")) != null) && (s.length() > 0)) {
                number = Integer.parseInt(s);
            }
            if (((s = req.getParameter("page")) != null) && (s.length() > 0)) {
                page = Integer.parseInt(s);
            }
            if (((s = req.getParameter("position")) != null)
                    && (s.length() > 0)) {
                position = Integer.parseInt(s);
            }
            // check for bank statement all fields or no field set
            // exclude the special case starting the year with
            // number 1, page 1 and position 0
            if ((bankAccountId != 0) || (number != 0) || (page != 0)
                    || (position != 0)) {
                if ((number == 0)
                        || (page == 0)
                        || ((position == 0) && (number != 1) && (position != 1))) {
                    throw new AppException(
                            "Bankkonto, Kontoauszug, Seite und "
                                    + "Position müssen alle oder gar nicht gesetzt werden!");
                }
            }
            if ((s = (String) session.getAttribute("month")) != null) {
                // default: this year
                String year = (new Integer((new GregorianCalendar())
                        .get(Calendar.YEAR))).toString();
                try {
                    year = (String) session.getAttribute("year");
                } catch (Exception e) {
                    // use default
                }
                bookingDate = bookingDate + "." + s + "." + year;
            }
            /*
             * UPDATE
             */
            if (req.getParameter("update") != null) {
                Booking booking = Booking.getBookingById(id);
                String oldBookingDate = booking.getBookingDateAsString()
                        .substring(0, 10);
                Money money = new Money(gross);
                BankStatement bankStatement = null;
                if (bankAccountId != 0) {
                    bankStatementId = booking.getBankStatementId();
                    if (bankStatementId > 0) {
                        bankStatement = BankStatement
                                .getBankStatementById(bankStatementId);
                        if ((bankAccountId != bankStatement.getBankAccountId())
                                || (number != bankStatement.getNumber())
                                || (page != bankStatement.getPage())
                                || (position != bankStatement.getPosition())) {
                            // something has changed
                            /*
                             * Before removing the old bank statement entry (to
                             * create the changed bank statement entry as new)
                             * we have to check that the new (changed) bank
                             * entry doesn't exist. Otherwise we get an error
                             * and having an inconsistent state.
                             */
                            if (BankStatement.getBankStatement(bankAccountId,
                                    number, page, position) != null) {
                                throw new AppException("Den Kontoauszug "
                                        + number + ", " + "Seite " + page
                                        + " und Position " + position
                                        + " gibt es bereits!");
                            }
                            bankStatement.remove();
                            bankStatementId = 0;
                        }
                    }
                    // no-one exist before, or something is
                    // differnet and
                    // the bank statement was removed
                    if (bankStatementId == 0) {
                        bankStatement = new BankStatement();
                        bankStatement.create(bankAccountId, number, page,
                                position);
                        bankStatementId = bankStatement.getId();
                    }
                } else {
                    // no bank statement, check & remove if
                    // before was one existing
                    if (booking.getBankStatementId() > 0) {
                        bankStatement = BankStatement
                                .getBankStatementById(booking
                                        .getBankStatementId());
                        bankStatement.remove();
                    }
                    bankStatementId = 0;
                }
                booking.update(money.getInCents(Money.ROUND_MERCANTILE),
                        description, bankStatementId, accountId, bookingDate,
                        new GregorianCalendar(), user, vat);
                url = res
                        .encodeRedirectURL("Booking.jsp?id=" + booking.getId());
                s = "";
                if (!booking.getBookingDateAsString()
                        .startsWith(oldBookingDate)) {
                    s = "<br><b><font color=darkviolet>Achtung: Das Datum wurde von "
                            + oldBookingDate
                            + " auf "
                            + booking.getBookingDateAsString().substring(0, 10)
                            + " geändert!</font>";
                }
                session.setAttribute("message", "Die Buchung wurde geändert."
                        + s);
                /*
                 * REMOVE
                 */
            } else if (req.getParameter("remove") != null) {
                Booking booking = Booking.getBookingById(id);
                bankStatementId = booking.getBankStatementId();
                if (bankStatementId > 0) {
                    BankStatement bankStatement = BankStatement
                            .getBankStatementById(bankStatementId);
                    bankStatement.remove();
                }
                booking.remove();
                session.setAttribute("message", "Die Buchung wurde gelöscht.");
                url = res.encodeRedirectURL("ListBookings.jsp");
                /*
                 * NEW
                 */
            } else if (req.getParameter("new") != null) {
                BankStatement bankStatement = null;
                if (bankAccountId != 0) {
                    bankStatement = new BankStatement();
                    bankStatement.create(bankAccountId, number, page, position);
                }
                if ((req.getParameter("vat")).length() == 0) {
                    // VAT input is empty, use account's
                    // VAT as default
                    vat = (Account.getAccountById(accountId)).getVat();
                }
                Booking booking = new Booking();
                Money money = new Money(gross);
                booking.setGross(money.getInCents(Money.ROUND_MERCANTILE));
                booking.setDescription(description);
                if (bankStatement != null) {
                    booking.setBankStatementId(bankStatement.getId());
                }
                booking.setAccountId(accountId);
                booking.setBookingDate(bookingDate);
                booking.setUser(user);
                booking.setVat(vat);
                booking.setDate(new GregorianCalendar());
                booking.create();
                session.setAttribute("message", "Die Buchung wurde angelegt.");
                url = res
                        .encodeRedirectURL("Booking.jsp?id=" + booking.getId());
            }
            res.sendRedirect(url);
        } catch (AppException ae) {
            session.setAttribute("error", ae.getMessage());
            url = res.encodeRedirectURL("Booking.jsp?id=" + id);
            res.sendRedirect(url);
        } catch (Exception e) {
            out.println("Fehler: " + e + "<P>");
            e.printStackTrace(out);
        }
    }

    /** For serialization. */
    private static final long serialVersionUID = 1L;
}
