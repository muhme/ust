<!--
  BankStatementDetails.jsp - show bank statement details if used

  ust web application, Copyright (c) 2001 Heiko Lübbe, MIT License, https://github.com/muhme/ust
-->

<%@ page import="de.hlu.ust.*" %>
<%@ page pageEncoding="UTF-8" %>

<%
try {

    int id = 0;
    int bsi = 0;        // bankStatementId
    BankStatement bs = new BankStatement(); // only to have initialized
    String str = new String("");

    try {
        Integer i = new Integer (request.getParameter ("id"));
        id = i.intValue();
    }
    catch (Exception e) {
        // throw new Exception ("Kann den Parameter id nicht auslesen: " + e);
        id = 0;
    }

    if (id > 0) {
        try {
            // get booking by id
            Booking booking = Booking.getBookingById (id);
            bsi = booking.getBankStatementId();
        }
        catch (AppException ae) {
            session.setAttribute ("error", ae.getMessage() + " (ignoriert)");
        }
    }


    if (bsi != 0) {
        try {
            bs = BankStatement.getBankStatementById (bsi);
        }
        catch (AppException ae) {
            session.setAttribute ("error", ae.getMessage() + " (ignoriert)");
        }
    }

    if (bsi != 0) {
        str = "" + bs.getNumber();
    }
    out.println ("<TR><TD>Kontoauszug</TD>");
    out.println ("<TD><input name=\"number\" size=40 maxlength=12 title=\"Kontoauszugsnummer\"");
    out.println ("value=" + str + "></TD></TR>");

    if (bsi != 0) {
        str = "" + bs.getPage();
    }
    out.println ("<TR><TD>Kontoauszug:Seite</TD>");
    out.println ("<TD><input name=\"page\" size=40 maxlength=12 title=\"die Seitennummer des Kontoauszugs\"");
    out.println ("value=" + str + "></TD></TR>");

    if (bsi != 0) {
        str = "" + bs.getPosition();
    }
    out.println ("<TR><TD>Kontoauszug:Position</TD>");
    out.println ("<TD><input name=\"position\" size=40 maxlength=12 title=\"die Position auf der Seite\"");
    out.println ("value=" + str + "></TD></TR>");

}
catch (Throwable t) {
    out.println ("</TABLE><P><FONT COLOR=RED>Es wurde eine Ausnahme " + t + " geworfen: " + t.getMessage() + "</FONT></P>");
}
%>
