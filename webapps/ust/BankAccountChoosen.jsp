<!--
  ust - my VAT calculating project
  BankAccountChoosen.jsp - show all bank accounts nicknames in a select box
  hlu, Sep 30 2003 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
-->

<%@ page import="de.hlu.ust.*" %>
<%@ page pageEncoding="UTF-8" %>

<%

try {

    int id = 0;
    int bankAccountId = 0;
    int bankStatementId = 0;
    BankAccount bankAccount = new BankAccount();        // only to have initialized
    BankStatement bankStatement = new BankStatement();  // only to have initialized
    BankAccount[] bankAccounts = BankAccount.getAllBankAccounts();

    try {
        Integer i = new Integer (request.getParameter ("id"));
        id = i.intValue();
    }
    catch (Exception e) {
        // throw new Exception ("Kann den Parameter id nicht auslesen: " + e);
        // TODO: use any BankAccount in case of displaying an booking create error
        id = 1;
    }

    if (id > 0) {
        // get booking by id
        Booking booking = Booking.getBookingById (id);
        bankStatementId = booking.getBankStatementId();
        if (bankStatementId != 0) {
            bankStatement = BankStatement.getBankStatementById (bankStatementId);
            bankAccountId = bankStatement.getBankAccountId ();
        }
    }

    out.println ("<TR><TD>Bankkonto</TD>");
    out.println ("<TD><select name=\"bankAccountId\" size=1 title=\"optionale Auswahl eines Bankkontos\"><option value=0>keines");

    for (int i=0; i < bankAccounts.length; ++i) {
        out.println ("<option value=" + bankAccounts[i].getId());
        if ((bankAccountId != 0) && (bankAccountId == bankAccounts[i].getId())) {
            out.print (" selected");
        }
        out.println (">" + bankAccounts[i].getNickname());
    }
    out.println ("</select></TD></TR>");
}
catch (Throwable t) {
    out.println ("</TABLE><P><FONT COLOR=RED>Es wurde eine Ausnahme " + t + " geworfen: " + t.getMessage() + "</FONT></P>");
}
%>
