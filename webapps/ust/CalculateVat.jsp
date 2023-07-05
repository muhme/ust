<!--
  ust - my VAT calculating project
  CalculateVat.jsp - calculate VAT
  hlu, Jan 5 2007 - Jul 5 2023
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
   <TITLE>ust | Ust-Berechnung</TITLE>
   <link rel="stylesheet" type="text/css" href="formate.css">
</head>

<jsp:include page="Menu.jsp" flush="true" />

<%
try {

    String str = request.getParameter ("print");
    if ((str != null) && str.equals ("true")) {
        session.setAttribute ("print", str);
        print = true;
    } else {
        print = false;
    }
%>


<BODY>

<jsp:include page="Message.jsp" flush="true" />

<%
    int month = 0;              // the month, default: all months

    // 1st from request parameter "month"
    str = request.getParameter ("month");
    if (str != null) {
        try {
            Integer i = new Integer (request.getParameter ("month"));
            month = i.intValue();
        }   
        catch (Exception e) {
            // ignore, use default
        }
    } else {
        // 2nd try to get it from session attribute "month"
        try {
            Integer i = new Integer ((String)session.getAttribute ("month"));
            month = i.intValue();
        }
        catch (Exception e) {
            // ignore, use default
        }
    }
    
    out.print ("<CENTER><H1>Rechnung zur ");
    if (month != 0) {
        out.print ("USt-Voranmeldung " + Finance.months[month]);
    } else {
        out.print ("USt-Erklärung");
    }
    out.print (" " + Config.getBookingYear() );    
    out.println ("</H1>");

    int from = 1;    // default from January
    int to = 12;     // default until December
    
    if (month > 0 && month < 13) {
        // monthly
        from = month;
        to = month;
    } else if (month == 13) {
        // 1st quarter
        from = 1;
        to = 3;        
    } else if (month == 14) {
        // 2nd quarter
        from = 4;
        to = 6;
    } else if (month == 15) {
        // 3rd quarter
        from = 7;
        to = 9;
    } else if (month == 16) {
        // 4th quarter
        from = 10;
        to = 12;
    }    
    Booking bookings[] = Booking.getAllBookings (from, to, 0);

    int netIn7  = 0;
    int netIn16 = 0;
    int netIn19 = 0;
    int vatIn7  = 0;
    int vatIn16 = 0;
    int vatIn19 = 0;
    int vatOut = 0;
    int preVat = 0;

    if (!print) {
    out.println ("<form method=\"get\" action=\"CalculateVat.jsp\">");
    
%>
Monat: 
<!-- dynamic include -->
<% String pageUrl = "MonthChoosen.jsp?withQuarters=true&selectValue=" + month; %>
<jsp:include page="<%= pageUrl %>" flush="true"></jsp:include>
<%
        
    out.println (" &nbsp; <input type=submit value=Aktualisieren title=\"Auswahl anzeigen\"> &nbsp; ");
    out.println ("<input type=image name=print value=true src=printer.gif title=\"zur Druckansicht\">");
    out.println ("</form>");
    }

    for (int i=0; i < bookings.length; ++i) {
        Account account = Account.getAccountById (bookings[i].getAccountId());
        if (account.getKind() == Finance.IN && bookings[i].getVat() != 0.0 ) {
            if (bookings[i].getVat() == 7.0) {
                vatIn7 += bookings[i].calcVat();
                netIn7 += bookings[i].calcNet();
            } else if (bookings[i].getVat() == 16.0) {
                vatIn16 += bookings[i].calcVat();
                netIn16 += bookings[i].calcNet();
            } else if (bookings[i].getVat() == 19.0) {
                vatIn19 += bookings[i].calcVat();
                netIn19 += bookings[i].calcNet();
            } else {
                out.println ("<font color=red>Buchung <a href=Booking.jsp?id=" + bookings[i].getId() + ">" + bookings[i].getId() + "</a> ignoriert, USt=" + bookings[i].getVat() + "%!</font><br>");
            }
        } else if (account.getKind() == Finance.OUT) {
            vatOut += bookings[i].calcVat();
        } else if (account.getKind() == Finance.PREVAT) {
            preVat += bookings[i].calcVat() * -1;
        }
    }
    
    // USt-Vorauszahlungen nur bei der Jahresrechnung
    if ( month != 0 ) {
        preVat = 0;
    }

    out.println ("<TABLE BORDER ALIGN=CENTER><TR>");
    out.println ("<TH COLSPAN=4 ALIGN=CENTER>Cent-genau</TH></TR><TR>");
    out.println ("<TH>Art</TH>");
    out.println ("<TH>Vorsteuer</TH>");
    out.println ("<TH>Umsatzsteuer</TH>");
    out.println ("<TH>Netto</TH></TR><TR>");

    // only 7% or ...
    if (vatIn7 != 0 || netIn7 != 0) {
	out.println ("<TD>USt 7%</TD>");
	out.println ("<TD>&nbsp;</TD>");
	out.println ("<TD ALIGN=RIGHT>" + new Money (vatIn7) + "</TD>");
	out.println ("<TD ALIGN=RIGHT>" + new Money (netIn7) + "</TD></TR><TR>");
    }

    // ... or 16%
    if (vatIn16 != 0 || netIn16 != 0) {
	out.println ("<TD>USt 16%</TD>");
	out.println ("<TD>&nbsp;</TD>");
	out.println ("<TD ALIGN=RIGHT>" + new Money (vatIn16) + "</TD>");
	out.println ("<TD ALIGN=RIGHT>" + new Money (netIn16) + "</TD></TR><TR>");
    }

    // ... or 19%
    if (vatIn19 != 0 || netIn19 != 0) {
	out.println ("<TD>USt 19%</TD>");
	out.println ("<TD>&nbsp;</TD>");
	out.println ("<TD ALIGN=RIGHT>" + new Money (vatIn19) + "</TD>");
	out.println ("<TD ALIGN=RIGHT>" + new Money (netIn19) + "</TD></TR><TR>");
    }

    // zu verschiedenen USt-Sätzen, daher ist Netto nicht angegeben
    out.println ("<TD>Vorsteuer</TD>");
    out.println ("<TD ALIGN=RIGHT>" + new Money (vatOut) + "</TD>");
    out.println ("<TD>&nbsp;</TD>");
    out.println ("<TD>&nbsp;</TD></TR><TR>");

    // USt-Vorauszahlungen nur bei der Jahresberechnung berücksichtigen
    if ( month == 0 ) {
        out.println ("<TD>USt-Vorauszahlung</TD>");
        out.println ("<TD ALIGN=RIGHT>" + new Money (preVat) + "</TD>");
        out.println ("<TD>&nbsp;</TD>");
        out.println ("<TD>&nbsp;</TD></TR><TR>");
    }

    out.println ("<TD>Ausstehende USt</TD>");
    out.println ("<TD ALIGN=RIGHT>" + new Money (vatIn7 + vatIn16 + vatIn19 - vatOut - preVat) +
                                    "</TD>");
    out.println ("<TD>&nbsp;</TD>");
    out.println ("<TD>&nbsp;</TD></TR>");

    // Für das Finanzamt werden die steuerpflichtigen Umsätze ohne Cent angegeben.
    netIn7 = netIn7 - (netIn7 % 100);
    vatIn7 = netIn7 * 7 / 100;
    netIn16 = netIn16 - (netIn16 % 100);
    vatIn16 = netIn16 * 16 / 100;
    netIn19 = netIn19 - (netIn19 % 100);
    vatIn19 = netIn19 * 19 / 100;
    
    out.println ("<TH COLSPAN=4 ALIGN=CENTER>Bemessungsgrundlage Netto ohne Cents</TH></TR><TR>");

    // only 7% or ...
    if (vatIn7 != 0 || netIn7 != 0) {
	out.println ("<TD>USt 7%</TD>");
	out.println ("<TD>&nbsp;</TD>");
	out.println ("<TD ALIGN=RIGHT>" + new Money (vatIn7 ) + "</TD>");
	out.println ("<TD ALIGN=RIGHT>" + new Money (netIn7) + "</TD></TR><TR>");
    }

    // ... or 16%
    if (vatIn16 != 0 || netIn16 != 0) {
	out.println ("<TD>USt 16%</TD>");
	out.println ("<TD>&nbsp;</TD>");
	out.println ("<TD ALIGN=RIGHT>" + new Money (vatIn16) + "</TD>");
	out.println ("<TD ALIGN=RIGHT>" + new Money (netIn16) + "</TD></TR><TR>");
    }
    
    // ... or 19%
    if (vatIn19 != 0 || netIn19 != 0) {
	out.println ("<TD>USt 19%</TD>");
	out.println ("<TD>&nbsp;</TD>");
	out.println ("<TD ALIGN=RIGHT>" + new Money (vatIn19) + "</TD>");
	out.println ("<TD ALIGN=RIGHT>" + new Money (netIn19) + "</TD></TR><TR>");
    }
    
    // zu verschiedenen USt-Sätzen, daher ist Netto nicht angegeben
    out.println ("<TD>Vorsteuer</TD>");
    out.println ("<TD ALIGN=RIGHT>" + new Money (vatOut) + "</TD>");
    out.println ("<TD>&nbsp;</TD>");
    out.println ("<TD>&nbsp;</TD></TR><TR>");

    // USt-Vorauszahlungen nur bei der Jahresberechnung berücksichtigen
    if ( month == 0 ) {
        out.println ("<TD>USt-Vorauszahlung</TD>");
        out.println ("<TD ALIGN=RIGHT>" + new Money (preVat) + "</TD>");
        out.println ("<TD>&nbsp;</TD>");
        out.println ("<TD>&nbsp;</TD></TR><TR>");
    }
    
    out.println ("<TD>Ausstehende USt</TD>");
    out.println ("<TD ALIGN=RIGHT>" + new Money (vatIn7 + vatIn16 + vatIn19 - vatOut - preVat) +
                                    "</TD>");
    out.println ("<TD>&nbsp;</TD>");
    out.println ("<TD>&nbsp;</TD></TR>");

    out.println ("</TABLE>");

}
catch (Throwable t) {
    out.println ("</TABLE><P><FONT COLOR=RED>Es wurde eine Ausnahme " + t + " geworfen: " + t.getMessage() + "</FONT></P>");
}
%>
</CENTER>
</BODY>
</html>
