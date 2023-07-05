<!--
  ust - my VAT calculating project
  AccountChoosen.jsp - show all accounts and give a selection, called from Booking.jsp
  hlu, May 13 2001 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
-->

<%@ page import="de.hlu.ust.*" %>
<%@ page pageEncoding="UTF-8" %>

<%
try {
    int id = 0;
    int accountId = 0;
    Account account = new Account();    // only to have initialized
    Account[] accounts = Account.getAllAccounts();

    try {
        Integer i = new Integer (request.getParameter ("id"));
        id = i.intValue();
    }
    catch (Exception e) {
        // throw new Exception ("Kann den Parameter id nicht auslesen: " + e);
        // TODO: use any account in case of displaying an booking create error
        id = 1;
    }

    if (id > 0) {
        // get booking by id
        Booking booking = Booking.getBookingById (id);
        accountId = booking.getAccountId();
    }

    if (accountId > 0) {
        account = Account.getAccountById (accountId);
    }

    out.println ("<TR><TD>Konto</TD>");
    out.println ("<TD><select name=\"accountId\" size=1 title=Buchungskonto>");

    for (int i=0; i < accounts.length; ++i) {
        out.println ("<option value=" + accounts[i].getId());
        if ((accountId != 0) && (accountId == accounts[i].getId())) {
            out.print (" selected");
        }
        out.println (">" + accounts[i].getName());
    }
    out.println ("</select></TD></TR>");
}
catch (Throwable t) {
    out.println ("</TABLE><P><FONT COLOR=RED>Es wurde eine Ausnahme " + t + " geworfen: " + t.getMessage() + "</FONT></P>");
}
%>
