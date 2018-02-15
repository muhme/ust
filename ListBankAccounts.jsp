<!--
  ust - my VAT calculating project
  ListBankAccounts.jsp - show all bank accounts
  hlu, 2001 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
-->

<%@ page import="de.hlu.ust.*" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>

<html>
<head>
   <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
   <title>ust | Bankkonten</title>
   <link rel="stylesheet" type="text/css" href="formate.css">
</head>

<jsp:include page="Menu.jsp" flush="true" />


<body>

<jsp:include page="Message.jsp" flush="true" />

<%
try {

    BankAccount[] BankAccounts = BankAccount.getAllBankAccounts();
    out.print ("<center><H1>" + BankAccounts.length + " ");
    if (BankAccounts.length == 1) {
        out.print ("Bankkonto");
    } else {
        out.print ("Bankkonten");
    }
    out.println ("</H1>");

    if(!(new BankAccount()).isReadOnly()) {
        out.print ("<form method=\"post\" action=\"BankAccount.jsp?BankAccount=0\">");
        out.print ("<input type=\"submit\" name=\"new\" value=\"Neu Anlegen\" title=\"ein neues Bankkonto anlegen\">");
        out.print ("</form>");
    }

    if (BankAccounts.length > 0) {

        out.println ("<TABLE BORDER ALIGN=CENTER><TR>");
        out.println ("<TH>#</TH>");
        out.println ("<TH>Kurzbezeichnung</TH>");
        out.println ("<TH>Kontonummer</TH>");
        out.println ("<TH>BLZ</TH>");
        out.println ("<TH>Beschreibung</TH>");
        out.println ("</TR>");
        for (int i=0; i < BankAccounts.length; ++i) {
            out.println ("<TR>");
            out.print ("<TD><A HREF=BankAccount.jsp?id=" +
                        BankAccounts[i].getId() + " title=\"zum " + BankAccounts[i].getId() + ". Bankeintrag\">" +
                        BankAccounts[i].getId() + "</A></TD>");

            out.print (new TableData (BankAccounts[i].getNickname()));
            out.print (new TableData (BankAccounts[i].getAccountNumber()));
            out.print (new TableData (BankAccounts[i].getBankCode()));
            out.print (new TableData (BankAccounts[i].getDescription()));

            out.println ("</TR>");
        }
        out.println ("</TABLE>");
    }
}
catch (Throwable t) {
    out.println ("</TABLE><P><FONT COLOR=RED>Es wurde eine Ausnahme " + t + " geworfen: " + t.getMessage() + "</FONT></P>");
}
%>
</CENTER>
</body>
</html>
