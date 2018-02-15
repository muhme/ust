<!--
  ust - my VAT calculating project
  Account.jsp - show one banking account
  hlu, May 12 2001 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
-->

<%@ page import="de.hlu.ust.*" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<html>
<head>
   <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
   <TITLE>ust | Buchungskonto</TITLE>
   <link rel="stylesheet" type="text/css" href="formate.css">
</head>

<BODY>

<%@ include file="Menu.jsp" %>

<jsp:include page="Message.jsp" flush="true" />
<center>
<%
try {
    Account account;
    int number = 0;

    try {
        Integer i = new Integer (request.getParameter ("account"));
        number = i.intValue();
    }
    catch (Exception e) {
        // ignore, use number=0 as new account
    }

    if (number > 0) {
        // get account by number
        account = Account.getAccountByNumber (number);
        out.print ("<H1>Buchungskonto " + number + "</H1>");
    } else {
        // new account
        account = new Account();
        account.setName ("");
        account.setDescription ("");
        account.setKind (Finance.OUT);
        account.setVat (16.0);
        account.setNumber (1000);
        account.setUser (Config.getUserName());

        out.print ("<H1>Neues Buchungskonto anlegen</H1>");
    }
%>
    <form method="post" action="AccountMaintenanceS">

    <TABLE BORDER>

    <TR><TD>Name</TD>
        <TD><input name="name" size=40 maxlength=40 title="Name des Buchungskontos"
            value="<%= account.getName()%>"></TD></TR>

    <TR><TD>Beschreibung</TD>
        <TD><input name="description" size=40 maxlength=80 title="weitere Informationen zu diesem Buchungskonto"
            value="<%= account.getDescription()%>"></TD></TR>

    <TR><TD>Kontonummer</TD>
        <TD><input name="number" size=40 maxlength=12 title="Nummer des Buchungskontos"
            value="<%= account.getNumber()%>"></TD></TR>

    <input type="hidden" name="id" size=40
            value="<%= account.getId()%>">

    <TR><TD>Art</TD>
        <TD><select name="kind" size=1 title="Art des Buchungskontos"><option
<%
    out.println (" value=" + Finance.IN);
    if (account.getKind() == Finance.IN) {
       out.print (" selected");
    }
%>
            >Einnahme<option
<%
    out.println (" value=" + Finance.OUT);
    if (account.getKind() == Finance.OUT) {
       out.print (" selected");
    }
%>
            >Ausgabe<option
<%
    out.println (" value=" + Finance.NEUTRAL);
    if (account.getKind() == Finance.NEUTRAL) {
       out.print (" selected");
    }
%>
            >Neutral<option
<%
    out.println (" value=" + Finance.PREVAT);
    if (account.getKind() == Finance.PREVAT) {
       out.print (" selected");
    }
%>
            >Vorsteuer<option
<%
    out.println (" value=" + Finance.IN_CASH_BOX);
    if (account.getKind() == Finance.IN_CASH_BOX) {
       out.print (" selected");
    }
%>
            >Einlage Kasse<option
<%
    out.println (" value=" + Finance.OUT_CASH_BOX);
    if (account.getKind() == Finance.OUT_CASH_BOX) {
       out.print (" selected");
    }
%>
            >Entnahme Kasse
    </select></TD></TR>

    <TR><TD>Kontoauszug</TD>
        <TD><select name="bankStatement" size=1 title="Kontoauszug-Vorbelegung für Buchungen dieses Buchungskontos"><option
<%
    if (account.getHasBankStatement()) {
       out.print (" selected");
    }
%>
            value=true >Ja<option
<%
    if (!account.getHasBankStatement()) {
       out.print (" selected");
    }
%>
            value=false >Nein 
    </select></TD></TR>

    <TR><TD>Ust-Satz in %</TD>
        <TD><input name="vat" size=40 maxlength=2 title="Umsatzsteuersatz-Vorbelegung für Buchungen dieses Buchungskontos"
            value="<%= account.getVatAsString()%>"></TD></TR>

    <TR><TD>Angelegt am</TD>
        <TD><%= account.getDateAsString()%></TD></TR>

    <TR><TD>Angelegt von</TD>
        <TD><input name="user" size=40 maxlength=80 title="Bearbeiter, der dieses Buchungskonto angelegt oder zuletzt geändert hat"
            value="<%= account.getUser()%>"></TD></TR>

    <TR><TD>Interne ID</TD>
        <TD><%= account.getId()%></TD></TR>
    </TABLE>
    <p>
<%
    if (!(new Account()).isReadOnly()) {
        // buttons only if data file writable
        if (number > 0) {
            // only for an already existing account
            out.println
                ("<input type=\"submit\" name=\"update\" value=\"Aktualisieren\" title=\"Buchungskonto " + number + " aktualisieren\">");
        }
        out.println( " &nbsp; <input type=\"submit\" name=\"new\" value=\"Neu Anlegen\" title=\"ein neues Buchungskonto mit diesen Werten anlegen\"> &nbsp; " );
        if (number > 0) {
            // only for an already existing account
            out.println
                ("<input type=\"submit\" name=\"remove\" value=\"Löschen\" title=\"Buchungskonto " + number + " löschen\">");
        }
    }
}
catch (Throwable t) {
        out.println ("</TABLE><P><FONT COLOR=RED>Es wurde eine Ausnahme " + t + " geworfen: " + t.getMessage() + "</FONT></P>");
}
%>
    </p>
    </FORM>
</center>
</BODY>
</HTML>
