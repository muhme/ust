<!--
  ust - my VAT calculating project
  Booking.jsp - show one booking in detail
  hlu, May 13 2001 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
-->

<%@ page import="de.hlu.ust.*" %>
<%@ page import="java.lang.Integer" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>

<%!
    private static String[] months = { "Alle", "Januar", "Februar", "März", "April",
        "Mai", "Juni", "Juli", "August", "September", "Oktober",
        "November", "Dezember" };
%>

<html>

<BODY>
<head>
   <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
   <TITLE>ust | Buchung</TITLE>
   <link rel="stylesheet" type="text/css" href="formate.css">
</head>

<!--  static include -->
<%@ include file="Menu.jsp" %>

<jsp:include page="Message.jsp" flush="true" />

<center>
<%

try {
    Booking booking;
    int id = 0;         // default: new booking entry
    int month = 0;      // default: month and year are choosable for new booking entries
                                // default: this year (only used is month used)
    int year = (new GregorianCalendar()).get (Calendar.YEAR);   

    try {
        Integer i = new Integer ((String)session.getAttribute ("month"));
        month = i.intValue();
    }
    catch (Exception e) {
        // ignore, use default
    }
    try {
        Integer i = new Integer ((String)session.getAttribute ("year"));
        year = i.intValue();
    }
    catch (Exception e) {
        // ignore, use default
    }

    try {
        Integer i = new Integer (request.getParameter ("id"));
        id = i.intValue();
    }
    catch (Exception e) {
        // ignore, use id=0 as new booking
    }

    if (id > 0) {
        // get booking by id
        booking = Booking.getBookingById (id);
        out.print ("<H1>" + id + ". Buchung");
    } else {
        // new booking
        booking = new Booking();
        booking.setGross (0);
        booking.setDescription ("");
        // month and year aren't choosable?
        if (month != 0) {
            booking.setBookingDate ("1." + month + "." + year);
        }
        // else: use the actual date
        booking.setUser (Config.getUserName());

        out.print ("<H1>Neue Buchung");
    }

    // new booking and month and year given?
    if ((id == 0) && (month != 0)) {
        out.print (" " + months[month] + " " + (new Integer (year)));
    }

    out.println ("</H1>");

%>
    <form method="post" action="BookingMaintenanceS">

    <TABLE BORDER>

    <TR><TD>Brutto / Netto</TD>
        <TD><input name="gross" size=19 maxlength=19 title="der Bruttobetrag"
        value="<%= booking.getGrossAsString()%>">
        <%= new Money( booking.calcNet()).toString()%></TD></TR>

    <TR><TD>% / USt</TD>
        <TD><input name="vat" size=19 maxlength=4 title="der Umsatzsteuersatz in Prozent"
        value="<%= booking.getVatAsString()%>">
        <%= new Money( booking.calcVat()).toString()%></TD></TR>

    <TR><TD>Beschreibung</TD>
        <TD><input name="description" size=40 maxlength=80 title="weitere Angaben zur Buchung"
        value="<%= booking.getDescription()%>"></TD></TR>

    <!-- only the 1st 10 chars from "12.05.2001 12:00" -->
    <TR><TD>
<%
    // month and year choosable?
    if (month == 0) {
        out.print ("Buchungsdatum");
        out.println ("<TD><input name=\"bookingDate\" size=40 maxlength=80 title=\"das Buchungsdatum (das Datum des Geldflusses)\"");
        out.println ("value=\"" + (booking.getBookingDateAsString()).substring(0,10) + "\"></TD></TR>");
    } else {
        out.print ("Buchungstag");
        out.println ("<TD><input name=\"bookingDate\" size=2 maxlength=2 title=\"der Buchungstag (das Datum des Geldflusses)\"");
        out.println ("value=\"" + (booking.getBookingDateAsString()).substring(0,2) + "\"> " + (booking.getBookingDateAsString()).substring(0,10) + "</TD></TR>");
    }
%>

<!-- dynamic include -->
<jsp:include page="BankAccountChoosen.jsp" flush="true"></jsp:include>

<!-- dynamic include -->
<jsp:include page="BankStatementDetails.jsp" flush="true"></jsp:include>

<!-- dynamic include -->
<jsp:include page="AccountChoosen.jsp" flush="true"></jsp:include>

    <input type="hidden" name="id" size=40
        value="<%= booking.getId()%>">

    <TR><TD>Angelegt am</TD>
        <TD><%= booking.getDateAsString()%></TD></TR>

    <TR><TD>Angelegt von</TD>
        <TD><input name="user" size=40 maxlength=80 title="Bearbeiter, der den Eintrag angelegt oder zuletzt geändert hat"
        value="<%= booking.getUser()%>"></TD></TR>

    <TR><TD>Interne ID</TD>
        <TD><%= booking.getId()%></TD></TR>
    </TABLE>
<%
    if (!(new Booking()).isReadOnly()) {
        if (id > 0) {
        // only for an already existing booking
            out.println
            ("<input type=\"submit\" name=\"update\" value=\"Aktualisieren\" title=\"die Änderungen für diesen Buchungseintrag übernehmen\">");
        }
        out.println("<input type=\"submit\" name=\"new\" value=\"Neu Anlegen\" title=\"einen neuen Buchungseintrag mit diesen Eingaben anlegen\">");
        if (id > 0) {
        // only for an already existing booking
            out.println
                ("<input type=\"submit\" name=\"remove\" value=\"Löschen\" title=\"diesen Buchungseintrag löschen\">");
        }
    }

}
catch (Throwable t) {
    out.println ("</TABLE><P><FONT COLOR=RED>Es wurde eine Ausnahme " + t + " geworfen: " + t.getMessage() + "</FONT></P>");
}
%>
    </FORM>
    </center>
</BODY>
</HTML>
