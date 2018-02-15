<!--
  ust - my VAT calculating project
  Calculate.jsp - calculate the benefit
  hlu, May 21 2001 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
-->

<%@ page import="de.hlu.ust.*" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>

<%!
    private static boolean withVAT = false;

    static int calculateNet (int from, int to, int accountNumber)
                                                throws AppException {
        int sum = 0;
        Booking[] bookings = Booking.getAllBookings (from, to, accountNumber);
        for (int i=0; i < bookings.length; ++i) {
            sum += bookings[i].calcNet();
        }
        return sum;
    }

    static int calculateVat (int from, int to, int accountNumber)
                                                throws AppException {
        int sum = 0;
        Booking[] bookings = Booking.getAllBookings (from, to, accountNumber);
        for (int i=0; i < bookings.length; ++i) {
            sum += bookings[i].calcVat();
        }
        return sum;
    }
%>

<HTML>
<head>
   <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
   <TITLE>ust | E/Ü-Rechnung</TITLE>
   <link rel="stylesheet" type="text/css" href="formate.css">
</head>

<%@ include file="Menu.jsp" %>

<jsp:include page="Message.jsp" flush="true" />

<BODY>
<CENTER><H1>Einnahme/Überschussrechnung
<%
    out.print (" " + Config.getBookingYear() );
%>
</H1>

<%

try {
    Account[] accounts = Account.getAllAccounts();
    int sum;
    int netIn  = 0;
    int netOut = 0;
    int vatIn  = 0;
    int vatOut = 0;

    String param = request.getParameter ("print");
    if ((param != null) && param.equals ("true")) {
        session.setAttribute ("print", param);
    }
    param = request.getParameter ("withVAT");
    if ((param != null) && param.equals ("true")) {
        withVAT = true;
    } else {
        withVAT = false;
    }

    if (accounts.length > 0) {

        out.println ("<TABLE BORDER ALIGN=CENTER><TR>");
        out.println ("<TH>Kontoname</TH>");
        out.println ("<TH>Einnahme</TH>");
        out.println ("<TH>Ausgabe</TH>");
        if (withVAT) {
            out.println ("<TH>Umsatzsteuer</TH>");
            out.println ("<TH>Vorsteuer</TH>");
        }
        out.println ("</TR>");
        for (int i=0; i < accounts.length; ++i) {
            if (!withVAT && ((accounts[i].getKind() == Finance.PREVAT) ||
                            (accounts[i].getKind() == Finance.NEUTRAL))) {
                continue;
            }
            out.println ("<TR>");
            out.print (new TableData (accounts[i].getName()));
            if ((accounts[i].getKind() == Finance.IN) ||
                   withVAT && ((accounts[i].getKind() == Finance.NEUTRAL))) {
                sum = calculateNet (1, 12, accounts[i].getNumber());
                out.print ("<TD ALIGN=RIGHT>" + new Money (sum) + "</TD>");
                netIn += sum;
            } else {
                out.print (new TableData (""));
            }
            if (accounts[i].getKind() == Finance.OUT) {
                sum = calculateNet (1, 12, accounts[i].getNumber());
                out.print ("<TD ALIGN=RIGHT>" + new Money (sum) + "</TD>");
                netOut += sum;
            } else if (accounts[i].getKind() == Finance.PREVAT) {
                sum = calculateVat (1, 12, accounts[i].getNumber()) * -1;
                out.print ("<TD ALIGN=RIGHT>" + new Money (sum) + "</TD>");
                netOut += sum;
            } else {
                out.print (new TableData (""));
            }
            if (withVAT) {
                if ((accounts[i].getKind() == Finance.IN) ||
                    ((accounts[i].getKind() == Finance.NEUTRAL))) {
                    sum = calculateVat (1, 12, accounts[i].getNumber());
                    out.print ("<TD ALIGN=RIGHT>" + new Money (sum) + "</TD>");
                    vatIn += sum;
                } else {
                    out.print (new TableData (""));
                }
                if (accounts[i].getKind() == Finance.OUT) {
                    sum = calculateVat (1, 12, accounts[i].getNumber());
                    out.print ("<TD ALIGN=RIGHT>" + new Money (sum) +
                                                                    "</TD>");
                    vatOut += sum;
                } else {
                    out.print (new TableData (""));
                }
            }
            out.println ("</TR>");
        }
        if (withVAT) {
            netIn += vatIn;
            out.println ("<TR><TD>Vorsteuer</TD>" +
                        "<TD ALIGN=RIGHT>" + new Money (vatIn) + "</TD>" +
                        "<TD>&nbsp;</TD><TD>&nbsp;</TD><TD>&nbsp;</TD></TR>");
            netOut += vatOut;
            out.println ("<TR><TD>Umsatzsteuer</TD><TD>&nbsp;</TD>" +
                        "<TD ALIGN=RIGHT>" + new Money (vatOut) +
                        "<TD>&nbsp;</TD><TD>&nbsp;</TD></TR>");
        }
        out.print ("<TR><TD><B>Summe</B></TD>" + "<TD ALIGN=RIGHT><B>" +
                                        new Money (netIn) + "</B></TD>");
        out.println ("<TD ALIGN=RIGHT><B>" + new Money (netOut) + "</B></TD>");
        if (withVAT) {
            out.println ("<TD ALIGN=RIGHT><B>" + new Money (vatIn) + "</B></TD>");
            out.println ("<TD ALIGN=RIGHT><B>" + new Money (vatOut) + "</B></TD>");
        }
        out.println ("</TR>");
        out.println ("<TR><TD><B>Differenz</B></TD><TD COLSPAN=" +
                (withVAT ? "4" : "2") + " ALIGN=CENTER><B>" +
                new Money (netIn - netOut) + "</B></TD></TR>");
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
