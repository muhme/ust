<!--
  ust - my VAT calculating project
  BankAccount.jsp - show one bank account entry
  hlu, 2001 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
-->

<%@ page import="de.hlu.ust.*" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>

<html>
<head>
   <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
   <TITLE>ust | Bankkonto</TITLE>
   <link rel="stylesheet" type="text/css" href="formate.css">
</head>

<BODY>

<%@ include file="Menu.jsp" %>

<CENTER>
<%
try {
    BankAccount bankAccount;
    int id = 0;

    try {
        Integer i = new Integer (request.getParameter ("id"));
        id = i.intValue();
    }
    catch (Exception e) {
        // ignore, use id=0 as new BankAccount
    }

    if (id > 0) {
        // get BankAccount by id
        bankAccount = BankAccount.getBankAccountById (id);
        out.print ("<H1>Bankkonto " + bankAccount.getNickname() + "</H1>");
    } else {
        // new BankAccount
        bankAccount = new BankAccount();
        bankAccount.setAccountNumber("");
        bankAccount.setBankName ("");
        bankAccount.setNickname ("");
        bankAccount.setDescription ("");
        bankAccount.setBankCode ("");
        bankAccount.setIBAN ("");
        bankAccount.setUser (Config.getUserName());

        out.print ("<H1>Neues Bankkonto</H1>");
    }
%>
    <form method="post" action="BankAccountMaintenanceS">

    <TABLE>

    <TR><TD>Kontonummer</TD>
        <TD><input name="accountNumber" size=40 maxlength=40 title="die Kontonummer"
            value="<%= bankAccount.getAccountNumber()%>"></TD></TR>

    <TR><TD>Institut</TD>
        <TD><input name="bankName" size=40 maxlength=40 title="der vollständige Name der Bank"
            value="<%= bankAccount.getBankName()%>"></TD></TR>

    <TR><TD>Kurzbezeichnung</TD>
        <TD><input name="nickname" size=40 maxlength=40 title="die Kurzbezeichnung des Kontos"
            value="<%= bankAccount.getNickname()%>"></TD></TR>

    <TR><TD>Beschreibung</TD>
        <TD><input name="description" size=40 maxlength=80 title="weitere Angaben zu diesem Bankkonto"
            value="<%= bankAccount.getDescription()%>"></TD></TR>

    <TR><TD>BLZ</TD>
        <TD><input name="bankCode" size=40 maxlength=40 title="die achtstellige Bankleitzahl"
            value="<%= bankAccount.getBankCode()%>"></TD></TR>

    <TR><TD>IBAN</TD>
        <TD><input name="iban" size=40 maxlength=40 title="die internationale Kontonummer"
            value="<%= bankAccount.getIBAN()%>"></TD></TR>

    <input type="hidden" name="id" size=40
            value="<%= bankAccount.getId()%>">

    <TR><TD>Angelegt am</TD>
        <TD><%= bankAccount.getDateAsString()%></TD></TR>

    <TR><TD>Angelegt von</TD>
        <TD><input name="user" size=40 maxlength=80 title="Bearbeiter, der den Eintrag angelegt oder zuletzt geändert hat"
            value="<%= bankAccount.getUser()%>"></TD></TR>

    <TR><TD>Interne ID</TD>
        <TD><%= bankAccount.getId()%></TD></TR>
    </TABLE>
    <p>
<%
    if (!(new BankAccount()).isReadOnly()) {
        // all buttons only if writable
        if (id > 0) {
            // only for an already existing BankAccount
            out.println
                ("<input type=\"submit\" name=\"update\" value=\"Aktualisieren\" title=\"die Werte für diesen Eintrag übernehmen\"> &nbsp; ");
        }
        out.println("<input type=\"submit\" name=\"new\" value=\"Neu Anlegen\" title=\"einen neuen Eintrag mit diesen Werten anlegen\"> &nbsp;");
        if (id > 0) {
            // only for an already existing BankAccount
            out.println
                    ("<input type=\"submit\" name=\"remove\" value=\"Löschen\" title=\"diesen Eintrag löschen\">");
        }
    }
}
catch (Throwable t) {
    out.println ("</TABLE><P><FONT COLOR=RED>Es wurde eine Ausnahme " + t + " geworfen: " + t.getMessage() + "</FONT></P>");
}
%>
    </p>
    </FORM>
</CENTER>
</BODY>
</HTML>
