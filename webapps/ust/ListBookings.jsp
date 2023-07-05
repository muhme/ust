<!--
  ust - my VAT calculating project
  ListBookings.jsp - show all bookings
  hlu, May 12 2001 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
-->

<%@ page import="de.hlu.ust.*" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>

<%!

    // 1st three cashTypes are followed by bank accounts
    private final int CASH_TYPE_ALL = 0;
    private final int CASH_TYPE_ONLY_CASH = 1;
    private final int CASH_TYPE_WITHOUT_CASH = 2;

    // four possibilities for taxTypes
    private final int TAX_TYPE_ALL   = 0; // alle Einträge
    private final int TAX_TYPE_SALES = 1; // nur USt
    private final int TAX_TYPE_PREVAT = 2; // nur VSt
    private final int TAX_TYPE_BOTH  = 3; // USt+VSt

    private static boolean print = false;

    // We start with showing all bookings.
    private int cashType = CASH_TYPE_ALL;
    private int taxType  = TAX_TYPE_ALL;

    /**
      * get a selection from all bookings, only cash bookings or bookings w/o cash
      */
    public static String chooseCashType (String selectName, int selectValue, String cashTypes[]) {

        String result = "<select name=\"" + selectName + "\" size=1 title=\"Geldfluss auswählen\">";

        for (int i=0; i < cashTypes.length; ++i) {
            result += "<option value=" + i;
            if (i == selectValue) {
                result += " selected";
            }
            result += ">" +cashTypes[i];
        }
        result += "</select>";

        return result;
    }

    /**
      * Get a selection on all, only sales tax, only input tax or
      * sales and input tax entries.
      */
    public static String chooseTaxType (String selectName, int selectValue) {
    final String[] taxTypes = { "Alle", "USt", "VSt", "USt + VSt" };
        String result = "<select name=\"" + selectName + "\" size=1 title=\"Umsatz- und Vorsteuer auswählen\">";

        for (int i=0; i < taxTypes.length; ++i) {
            result += "<option value=" + i;
            if (i == selectValue) {
                result += " selected";
            }
            result += ">" + taxTypes[i];
        }
        result += "</select>";

        return result;
    }
%>

<HTML>
<head>
   <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
   <TITLE>ust | Buchungen</TITLE>
   <link rel="stylesheet" type="text/css" href="formate.css">
</head>

<jsp:include page="Menu.jsp" flush="true" />

<%

try {

    // create a choose-box which bookings are to show
    // Alle | Nur Kasse | Ohne Kasse (d.h. alle mit BankStatement) | Bankkonto 1 | Bankkonto 2 ...
    
    BankAccount[] BankAccounts = BankAccount.getAllBankAccounts();
    int numberOfCashTypes;

    if (BankAccounts.length > 1) {
        numberOfCashTypes = 3 + BankAccounts.length;
    } else {
        numberOfCashTypes = 3;
    }
    String[] cashTypes = new String[numberOfCashTypes];
    cashTypes[0] = "Alle";
    cashTypes[1] = "Nur Kasse";
    cashTypes[2] = "Nur Bank";
    if (BankAccounts.length > 1) {
        for (int i = 0; i < BankAccounts.length; ++i) {
            cashTypes[3+i] = BankAccounts[i].getNickname();
        }
    }

    String str = request.getParameter ("print");
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
%>

<BODY>

<%
    int from = 1;                     // default: list months from january
    int to = 12;                      // default: list until december
    int accountNumber = 0;            // list only this account or all with 0

    // 1st: default, show all bookings (cashType independent)
    cashType = CASH_TYPE_ALL;
    // 2nd: from request parameter
    String attr = request.getParameter ("cashType");
    if (attr != null) {
        // remember the request parameter as session attribute
        session.setAttribute ("cashType", attr);
    } else {
        // 3rd: from session (neccessary coming back from showing one booking)
        attr = (String)session.getAttribute ("cashType");
    }
    try {
        Integer i = new Integer (attr);
        cashType = i.intValue();
    }
    catch (Exception e) {
        // ignore, use all bookings
    }

    // 1st: default, show all bookings (taxType independent)
    taxType = TAX_TYPE_ALL;
    // 2nd: from request parameter
    attr = request.getParameter ("taxType");
    if (attr != null) {
        // remember the request parameter as session attribute
        session.setAttribute ("taxType", attr);
    } else {
        // 3rd: from session (neccessary coming back from showing one booking)
        attr = (String)session.getAttribute ("taxType");
    }
    try {
        Integer i = new Integer (attr);
        taxType = i.intValue();
    }
    catch (Exception e) {
        // ignore, use all bookings
    }

    // 1st from request parameter "from"
    str = request.getParameter ("from");
    if (str != null) {
        try {
            Integer i = new Integer (str);
            from = i.intValue();
        }
        catch (Exception e) {
            // ignore, use default
        }
    } else {
        // 2nd try to get it from session attribute "month"
        try {
            Integer i = new Integer ((String)session.getAttribute ("month"));
            // not choosable?
            if (i.intValue() != 0) {
                from = i.intValue();
            }
            // else use default
        }
        catch (Exception e) {
            // ignore, use default
        }
    }
    
    // 1st from request parameter "to"
    str = request.getParameter ("to");
    if (str != null) {
        try {
            Integer i = new Integer (str);
            to = i.intValue();
        }
        catch (Exception e) {
            // ignore, use default
        }
    } else {
        // 2nd try to get it from session attribute "month"
        try {
            Integer i = new Integer ((String)session.getAttribute ("month"));
            // not choosable?
            if (i.intValue() != 0) {
                to = i.intValue();
            }
            // else use default
        }
        catch (Exception e) {
            // ignore, use default
        }
    }

    try {
    Integer i = new Integer (request.getParameter ("accountNumber"));
    accountNumber = i.intValue();
    }
    catch (Exception e) {
    // ignore, use all accounts
    accountNumber = 0;
    }

    Booking[] b = Booking.getAllBookings (from, to, accountNumber);

    out.print ("<CENTER><H1>");

    if (cashType == CASH_TYPE_ONLY_CASH) {
        out.print ("Kasseneinträge aus ");
    } else if (cashType == CASH_TYPE_WITHOUT_CASH) {
        out.print ("Bankeinträge aus ");
    } else if (cashType > CASH_TYPE_WITHOUT_CASH) {
        out.print ("Bankeinträge " + cashTypes[cashType] + " aus ");
    }

    out.print(b.length + " ");
    if (b.length == 1) {
        out.print ("Buchung");
    } else {
        out.print ("Buchungen");
    }
    if ((from != 1) || (to != 12)) {
        if (from == to) {
            out.print (" " + Finance.months[from]);
        } else {
            out.print (" von " + Finance.months[from] + " bis " + Finance.months[to]);
        }
    }
    out.print (" " + Config.getBookingYear() );
    if (accountNumber != 0) {
        Account account = Account.getAccountByNumber (accountNumber);
        out.print (" für das Konto " + accountNumber + " " + account.getName());
    }
    
    if (taxType == TAX_TYPE_SALES) {
        out.print (" (nur Umsatzsteuer)");
    } else if (taxType == TAX_TYPE_PREVAT) {
        out.print (" (nur Vorsteuer)");
    } else if (taxType == TAX_TYPE_BOTH) {
       out.print (" (nur USt und VSt)");
    }

    out.println ("</H1></CENTER>");
%>

<jsp:include page="Message.jsp" flush="true" />

<P>
<%
if (!print) {
    out.println ("<center>");
    out.print   ("<form method=\"get\" action=\"ListBookings.jsp\">");

%>
Von: 
<!-- dynamic include -->
<jsp:include page="<%= "MonthChoosen.jsp?startJanuary=true&selectName=from&selectValue=" + from %>" flush="true"></jsp:include>
Bis: 
<!-- dynamic include -->
<jsp:include page="<%= "MonthChoosen.jsp?startJanuary=true&selectName=to&selectValue=" + to %>" flush="true"></jsp:include>
<%

    Account[] accounts = Account.getAllAccounts();
    out.println ("<select name=\"accountNumber\" size=1 title=\"Buchungskonto auswählen\">");
    out.print   ("<option value=0");
    if (accountNumber == 0) {
        out.print (" selected");
    }
    out.println ("> Alle");

    for (int i=0; i < accounts.length; ++i) {
       out.println ("<option value=" + accounts[i].getNumber());
    if (accountNumber == accounts[i].getNumber()) {
        out.print (" selected");
    }
    out.println (">" + accounts[i].getName());
    }
    out.println ("</select>");

//    out.println ("<select name=\"print\" size=1>");
//    out.println ("<option value=true>Druck");
//    out.println ("<option value=false selected>Nein");
//    out.println ("</select>");

    out.println (chooseCashType ("cashType", cashType, cashTypes));

    out.println (chooseTaxType ("taxType", taxType));


    out.println ("<input type=\"submit\" value=Aktualisieren title=\"Auswahl anzeigen\">");

    out.println ("<input type=\"image\" name=print value=true src=printer.gif title=\"zur Druckansicht\">");
    out.println ("</form>");

    if (!(new Booking()).isReadOnly()) {
        out.println ("<form method=\"post\" action=\"Booking.jsp?id=0\">");    
        out.println ("<input type=\"submit\" name=\"new\" value=\"Neu Anlegen\" title=\"eine neue Buchung anlegen\">");
        out.println ("</form>");
    }


    out.println ("</center>");
}
%>
</P>
<P>
<%
if (b.length > 0) {

    Money grossMoney = new Money();     // gross sum
    Money netMoney = new Money();       // netto sum
    Money vatMoney = new Money();       // VAT sum
    int entries = 0;                    // shown entries
    String val;                         // value to print
%>

    <TABLE BORDER ALIGN=CENTER>
<%
    out.println ("<TR><TH>#</TH>");
    out.println ("<TH>Brutto</TH>");
    out.println ("<TH>Netto</TH>");
    out.println ("<TH>%</TH>");
    out.println ("<TH>USt</TH>");
    out.println ("<TH>Datum</TH>");
    out.println ("<TH>Kommentar</TH>");
    out.println ("<TH>Ausz</TH>");
    out.println ("<TH>Seite</TH>");
    out.println ("<TH>Pos</TH>");
    out.println ("</TR>");
    for (int i=0; i < b.length; ++i) {

        Account account = Account.getAccountById (b[i].getAccountId());

        // only cashs?
        if ((cashType == CASH_TYPE_ONLY_CASH) && b[i].getBankStatementId() != 0) {
            continue;
        }
        // without cashs?
        if ((cashType >= CASH_TYPE_WITHOUT_CASH) && b[i].getBankStatementId() == 0) {
            continue;
        }
        if ((taxType == TAX_TYPE_SALES) && ( ! b[i].hasVat() || ( account.getKind() == Finance.PREVAT))) {
            continue;
        }
        if ((taxType == TAX_TYPE_PREVAT) && (account.getKind() != Finance.PREVAT)) {
            continue;
        }
        if ((taxType == TAX_TYPE_BOTH) && ! b[i].hasVat() && (account.getKind() != Finance.PREVAT)) {
            continue;
        }
        // only one bank account choosen?   
        if ((cashType > CASH_TYPE_WITHOUT_CASH) &&  b[i].getBankStatementId() != 0) {
            try {
                BankStatement bs = BankStatement.getBankStatementById (b[i].getBankStatementId());
                BankAccount ba = BankAccount.getBankAccountById (bs.getBankAccountId());
                if (!ba.getNickname().equals (cashTypes[cashType])) {
                    continue;
                }
            }
            catch (Exception e) {
                out.println ("Probleme beim Vergleichen des Bankkontos!");
            }
        }
    
        out.println ("<TR>");
        entries++;

    // *** #

        out.println ("<TD ALIGN=RIGHT>");
        if (print) {
            out.println (b[i].getId());
        } else {
            out.println ("<A HREF=Booking.jsp?id="
            + b[i].getId() + " title=\"zur " + b[i].getId() + ". Buchung\">" + b[i].getId() + "</A>");
        }
        out.println ("</TD>");

        val = b[i].getGrossAsString();
        if (account.getKind() == Finance.PREVAT) {
            vatMoney.add ( b[i].getGross() );
            grossMoney.add (b[i].getGross() ); // new
            val = "+" + val; // green
        }
        if ((account.getKind() == Finance.IN) || (account.getKind() == Finance.IN_CASH_BOX)) {
            grossMoney.add (b[i].getGross() );
            netMoney.add ( b[i].calcNet() );
            vatMoney.add ( b[i].calcVat() );
            val = "+" + val; // green
        } else if ((account.getKind() == Finance.OUT) || (account.getKind() == Finance.OUT_CASH_BOX)) {
            grossMoney.add ( -1 * b[i].getGross() );
            netMoney.add ( -1 * b[i].calcNet() );
            vatMoney.add ( -1 * b[i].calcVat() );
            val = "-" + val; // red
        }
        // else: black

        // *** Brutto

        val = TableData.colorize (val);
        out.print (TableData.right (val));
        
        if (account.getKind() == Finance.PREVAT) {
            val = null;
        } else {
            val = new Money( b[i].calcNet()).toString();
        }
        if ((account.getKind() == Finance.IN) ||  (account.getKind() == Finance.IN_CASH_BOX)) {
            val = "+" + val;    // green
        } else if ((account.getKind() == Finance.OUT) || (account.getKind() == Finance.OUT)) {
            val = "-" + val;    // red
        }
        // else: black

        // *** Netto

        val = TableData.colorize (val);
        out.print (TableData.right (val));


        // *** %
    
        val = account.getKind() != Finance.PREVAT ? b[i].getVatAsString() : null;
        out.print (TableData.right (val));

        if (account.getKind() == Finance.PREVAT) {
            val = "+" + b[i].getGrossAsString();
        } else {
            val = new Money( b[i].calcVat()).toString();
        }
        if ((account.getKind() == Finance.IN) || (account.getKind() == Finance.IN_CASH_BOX)) {
            val = "+" + val;    // green
        } else if ((account.getKind() == Finance.OUT) || (account.getKind() == Finance.OUT_CASH_BOX)) {
            val = "-" + val;    // red
        }       
        // else: black

        // *** USt

        val = TableData.colorize (val);
        out.print (TableData.right (val));

        // *** Datum

        out.println ("<TD>" +
            // only the 1st 10 chars from "12.05.2001 12:00"
            (b[i].getBookingDateAsString()).substring(0,10) + "</TD>");

        // *** Kommentar

        out.println ("<TD>" + account.getName() + ": " + b[i].getDescription()+ "&nbsp;</TD>");

        // *** Ausz / Seite / Pos

        if (b[i].getBankStatementId() == 0) {
            out.println ("<TD>&nbsp;</TD><TD>&nbsp;</TD><TD>&nbsp;</TD>");
        } else {
            BankStatement bs = BankStatement.getBankStatementById (b[i].getBankStatementId());
            out.println ("<TD ALIGN=RIGHT>" + BankAccount.getBankAccountNickname (bs.getBankAccountId()) + " " + bs.getNumber() + "</TD>");
            out.println ("<TD ALIGN=RIGHT>" + bs.getPage() + "</TD>");
            out.println ("<TD ALIGN=RIGHT>" + bs.getPosition() + "</TD>");
        }
        out.println ("</TR>");
    }

    out.print ( "<TR><TD>&nbsp;</TD>" +
                TableData.right ( "<B>" + TableData.colorize ("+" + grossMoney) + "</B>" ) +
                TableData.right ( "<B>" + TableData.colorize ("+" + netMoney) + "</B>" ) +
                "<TD>&nbsp;</TD>" +
                TableData.right ( "<B>" + TableData.colorize ("+" + vatMoney) + "</B>" ) +
                "<TD>&nbsp;</TD><TD><B>");
    if (entries > 1) {
        out.println (entries + " Buchungen");
    }
    out.println ("&nbsp;</B></TD><TD>&nbsp;</TD><TD>&nbsp;</TD><TD>&nbsp;</TD></TR>");
    out.println ("</TABLE>");
    }
}
catch (Throwable t) {
    out.println ("</TABLE><P><FONT COLOR=RED>Es wurde eine Ausnahme " + t + " geworfen: " + t.getMessage() + "</FONT></P>");
}
%>
</body>
</html>
