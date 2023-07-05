<!--
  ust - my VAT calculating project
  ListBankStatements.jsp - show all bank statements
  hlu, May 12 2001 - Jul 5 2023
-->

<%@ page import="de.hlu.ust.*" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>

<HTML>
<head>
   <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
   <TITLE>ust | Bankeintr&auml;ge</TITLE>
   <link rel="stylesheet" type="text/css" href="formate.css">
</head>

<jsp:include page="Menu.jsp" flush="true" />

<BODY>

<jsp:include page="Message.jsp" flush="true" />

<%
try {
    int month = 0;              // month to list or 0 for all months
    String str;

    boolean print = false;
    str = request.getParameter ("print");
    if ( (str == null) && (request.getParameter ("print.x") != null)) {
        // ie doesn't support value for input type=image
        str = "true";
    }
    if ((str != null) && str.equals ("true")) {
        session.setAttribute ("print", str);
        print = true;
    } else {
        print = false;
    }

    // 1st from request parameter "month"
    str = request.getParameter ("month");
    if (str != null) {
        try {
            Integer i = new Integer ( str );
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

    BankStatement[] bs = BankStatement.getAllBankStatements( month == 0 ? 1 : month, month == 0 ? 12 : month );
    
    out.println ("<CENTER><H1>" + bs.length + " ");
    if (bs.length == 1) {
        out.print ("Bank-Eintrag ");
    } else {
        out.print ("Bank-Einträge ");
    }
    if ( month != 0 ) {
        out.println (Finance.months[month]);
    }
    out.print (" " + Config.getBookingYear() );
    out.println ("</H1>");

    if (!print) {
        out.print   ("<form method=\"get\" action=\"ListBankStatements.jsp\">");
        
%>
Monat:  
<!-- dynamic include -->
<% String pageUrl = "MonthChoosen.jsp?selectValue=" + month; %>
<jsp:include page="<%= pageUrl %>" flush="true"></jsp:include>
</td></tr>
<%        
        
        out.println (" &nbsp; <input type=submit value=Aktualisieren title=\"Auswahl anzeigen\"> &nbsp; ");
    
        out.println ("<input type=image name=print value=true src=printer.gif title=\"zur Druckansicht\">");
        out.println ("</form>");
    }

    if (bs.length > 0) {

        out.println ("<p><TABLE BORDER ALIGN=CENTER><TR>");
        out.println ("<TH>#</TH>");
        out.println ("<TH>Institut</TH>");
        out.println ("<TH>Kontonummer</TH>");
        out.println ("<TH>Nummer</TH>");
        out.println ("<TH>Seite</TH>");
        out.println ("<TH>Position</TH>");
        out.println ("<TH>Datum</TH>");
        out.println ("<TH>Beschreibung</TH>");
        out.println ("<TH>Betrag</TH>");
        out.println ("</TR>");
        for (int i=0; i < bs.length; ++i) {
            Booking booking = new Booking();
            Account account = new Account();
            try {
                booking = Booking.getBookingByBankStatementId (bs[i].getId());
                account = Account.getAccountById (booking.getAccountId());
            }
            catch (AppException ae) {
                // ignore the trouble at the moment and show one error message later
                session.setAttribute ("error", ae.getMessage());
            }

            BankAccount bankAccount = BankAccount.getBankAccountById (bs[i].getBankAccountId());

            out.print ("<TR>"); 

            out.println ("<TD ALIGN=RIGHT>");
            if (print) {
                out.println (booking.getId());
            } else {
               out.println ("<A HREF=Booking.jsp?id=" + booking.getId() + " title=\"zum " + booking.getId() + ". Bankeintrag\">" + booking.getId() + "</A>");
            }
            out.println ("</TD>");

            out.print (
                TableData.center (bankAccount.getBankName()) +
                TableData.right (bankAccount.getAccountNumber()) +
                TableData.right (bs[i].getNumber()) +
                TableData.right (bs[i].getPage()) +
                TableData.right (bs[i].getPosition()) +
                TableData.left (booking.getBookingDateAsString().substring(0,10)) +
                TableData.left (booking.getDescription()));

            String val =  booking.getGrossAsString();
            if ( account.getKind() == Finance.OUT ) {
                val = "-" + val;    // to get it red, if the value is positive
            } else if ( account.getKind() == Finance.IN ) {
                val = "+" + val;    // to get it green, if the value is positive
            }
            out.print ( TableData.right( TableData.colorize ( val ) ) );
        
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
