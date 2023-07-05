<!--
  ust - my VAT calculating project
  Menu.jsp - show the menu if print not set
  hlu, May 24 2001 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
-->

<%@ page import="
    de.hlu.ust.*,
    java.util.GregorianCalendar"
%>
<%@ page pageEncoding="UTF-8" %>

<%
try {

    String str;

    // read context-param's
    if (Config.isContextParamRead() == false) {

        boolean exception = false;

        if ((str = getServletContext().getInitParameter("user.name")) != null) {
            Config.setUserName (str);
        }
        if ((str = getServletContext().getInitParameter("UST_CURRENCY")) != null) {
            Config.setCurrency (str);
        }
        if ((str = getServletContext().getInitParameter("UST_DATA")) != null) {
            Config.setFilePath (str);
        }
        if ((str = getServletContext().getInitParameter("UST_TAX_NUMBER")) != null) {
            Config.setTaxNumber (str);
        }
        if ((str = getServletContext().getInitParameter("UST_TAG")) != null) {
            Config.setTag (str);
        }
        try {
            // checking at first start to create empty data files
            Account account = new Account();
            Booking booking = new Booking();
            BankStatement bankStatement = new BankStatement();
            BankAccount bankAccount = new BankAccount();

            // only create the data files if all four data files are missing             
            if ( ! account.isReadable() && ! booking.isReadable() &&
                 ! bankStatement.isReadable() && ! bankAccount.isReadable() ) {
                 session.setAttribute("warning", "Dateien " + account.getDataFileName() + ", " +
                     booking.getDataFileName() + ", " + bankStatement.getDataFileName() + " und " +
                     bankAccount.getDataFileName() + " im Verzeichnis " + Config.getFilePath() +
                     " neu angelegt.");
                 account.createDataFile();
                 booking.createDataFile();
                 bankStatement.createDataFile();
                 bankAccount.createDataFile();
                 Config.createVersionFile();
            }
            
            // checking once for data-files version updates
            Config.checkDataFilesVersionUpdates();
        }
        // show the data update message and go on
        catch (AppException a) {
            exception = true;
            out.println ("</TABLE><P><FONT COLOR=RED>" + a.getMessage() + "</FONT></P>");
        }
        if (exception == false) {
            // set web.xml' context-param's once
            Config.setContextParamRead();
        }
    }

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
    
    if (print) {
        out.println ("<TABLE WIDTH=100% class=print><colgroup width=33% span=3></colgroup>");
        out.println ( "<TR>" +
            TableData.left (Config.getUserName()) +
            TableData.center ("Steuernummer " + Config.getTaxNumber()) +
            TableData.right (Finance.gregorianToString (new GregorianCalendar())));

        session.removeAttribute ("print");

    } else {
        String readonly = "";
        if ((new Account()).isReadOnly() && (new Booking()).isReadOnly() &&
             (new BankStatement()).isReadOnly() && (new BankAccount()).isReadOnly() ) {
            readonly="&nbsp;<img src=readonly.png title=Read-Only alt=Read-Only>";
        }
        String tag = Config.getTag();
        out.println ("<TABLE width=100% CLASS=menu><colgroup width=12% span=8></colgroup>");
        out.println ( "<TR>" +
            TableData.center( tag + " <b>&nbsp;" + Config.getBookingYear() + "&nbsp;</b>" + readonly) +
            TableData.center ("<A HREF=ListAccounts.jsp title=\"Liste aller Buchungskonten\">Konten</A><A HREF=\"ListAccounts.jsp?print=true\" title=\"Druckansicht\"> <IMG SRC=small_printer.gif BORDER=0 ALIGN=BOTTOM></A>") +
            TableData.center ("<A HREF=ListBookings.jsp title=\"Liste aller Buchungen\">Buchungen</A></TD>") +
            TableData.center ("<A HREF=ListBankStatements.jsp title=\"Liste aller Bankeinträge\">Bankeinträge</A></TD>") +
            TableData.center ("<A HREF=Calculate.jsp title=\"Einnahme/Überschussrechnung\">E/Ü</A> <A HREF=\"Calculate.jsp?print=true\" title=\"Druckansicht ohne USt\"><IMG SRC=small_printer.gif BORDER=0 ALIGN=BOTTOM> <A HREF=\"Calculate.jsp?withVAT=true\" title=\"inklusive Umsatzsteuer\">+USt</A> <A HREF=\"Calculate.jsp?print=true&withVAT=true\" title=\"Druckansicht mit USt\"><IMG SRC=small_printer.gif BORDER=0 ALIGN=BOTTOM></A>") +
            TableData.center ("<A HREF=CalculateVat.jsp title=\"Umsatzsteuer-Voranmeldung und -Erklärung\">USt-Berechnung</A></TD>") +
            TableData.center ("<A HREF=ListBankAccounts.jsp title=\"Liste aller Banken\">Bankkonten</A></TD>") +
            TableData.center ("<A HREF=Configuration.jsp title=\"Konfiguration anzeigen und ändern\">Konfiguration</A></TD>"));
    }

    out.println ("</TR>");
    out.println ("</TABLE>");

}
catch (Throwable t) {
    out.println ("</TABLE><P><FONT COLOR=RED>Es wurde eine Ausnahme " + t + " geworfen: " + t.getMessage() + "</FONT></P>");
}
%>
