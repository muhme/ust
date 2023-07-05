<!--
  ust - my VAT calculating project
  ListAccounts.jsp - show all accounts
  hlu, May 12 2001 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
-->

<%@ page import="de.hlu.ust.*" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>

<%!
    private static boolean print = false;
%>

<HTML>
<head>
   <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
   <TITLE>ust | Buchungskonten</TITLE>
   <link rel="stylesheet" type="text/css" href="formate.css">
</head>

<%
    String str = request.getParameter ("print");
    if ((str != null) && str.equals ("true")) {
        session.setAttribute ("print", str);
        print = true;
    } else {
        print = false;
    }
%>

<jsp:include page="Menu.jsp" flush="true" />

<BODY>

<jsp:include page="Message.jsp" flush="true" />

<%

try {

    Account[] accounts = Account.getAllAccounts();
    out.print ("<CENTER><H1>" + accounts.length + " ");
    if (accounts.length == 1) {
        out.print ("Buchungskonto");
    } else {
        out.print ("Buchungskonten");
    }
    out.println ("</H1>");

    if (!print && !(new Account()).isReadOnly()) {
        out.print ("<form method=\"post\" title=\"neues Buchungskonto anlegen\" action=\"Account.jsp?account=0\">");
        out.print ("<input type=\"submit\" name=\"new\" value=\"Neu Anlegen\">");
        out.print ("</form>");
    }

    if (accounts.length > 0) {

        out.println ("<TABLE BORDER ALIGN=CENTER><TR>");
        out.println ("<TH>#</TH>");
        out.println ("<TH>Name</TH>");
        out.println ("<TH>Art</TH>");
        out.println ("<TH>Beschreibung</TH>");
        out.println ("</TR>");
        for (int i=0; i < accounts.length; ++i) {
            out.println ("<TR>");
            out.print ("<TD><A HREF=Account.jsp?account=" +
                accounts[i].getNumber() + " title=\"zum Buchungskonto " + accounts[i].getNumber() + "\">" +
                accounts[i].getNumber() + "</A></TD>");

            TableData td = new TableData (accounts[i].getName());
            out.print (td);

            if (accounts[i].getKind() == Finance.IN) {
                out.print ("<TD>Einnahme</TD>");
            } else if (accounts[i].getKind() == Finance.OUT) {
                out.print ("<TD>Ausgabe</TD>");
            } else if (accounts[i].getKind() == Finance.PREVAT) {
                out.print ("<TD>VorSt</TD>");
            } else if (accounts[i].getKind() == Finance.NEUTRAL) {
                out.print ("<TD>Neutral</TD>");
            } else if (accounts[i].getKind() == Finance.IN_CASH_BOX) {
                out.print ("<TD>Einlage Kasse</TD>");
            } else if (accounts[i].getKind() == Finance.OUT_CASH_BOX) {
                out.print ("<TD>Entnahme Kasse</TD>");
            } else {
                out.print ("<TD>" + accounts[i].getKind() + "<TD>");
            }

            td.setContent (accounts[i].getDescription());
            out.print (td);
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
</BODY>
</html>
