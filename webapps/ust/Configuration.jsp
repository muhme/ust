<!--
  ust - my VAT calculating project
  Configuration.jsp - show and set the configuration
  hlu, Sep 21 2003 - Jul 5 2023
-->

<%@ page import="de.hlu.ust.*" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
     "http://www.w3.org/TR/html4/strict.dtd">
<HTML>
<head>
   <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
   <TITLE>ust | Konfiguration</TITLE>
   <link rel="stylesheet" type="text/css" href="formate.css">
</head>
<BODY>

<jsp:include page="Menu.jsp" flush="true" />

<CENTER><H1>Konfiguration</H1>

<jsp:include page="Message.jsp" flush="true" />

<p>
die Anwendung ust, in der Version <%= Finance.VERSION %>, vom <%= Finance.DATE %><br>
<a href="https://github.com/muhme/ust">https://github.com/muhme/ust</a>
</p>
<table>
    <tr><th>Eigenschaft</th><th>Wert</th></tr>
    <form method="get" action="Configuration.jsp">
<%
try {

    // default: selectable
    int month = 0;   

    // 1st try to get it from the request
    String str = request.getParameter ("month");
    if (str != null) {
        try {
            Integer i = new Integer (str);
            month = i.intValue();
        }
        catch (Exception e) {
            // ignore
        }
    } else {
        // 2nd try to get it from session
        try {
            Integer i = new Integer ((String)session.getAttribute ("month"));
            month = i.intValue();
        }
        catch (Exception e) {
        // ignore
        }
    }

    // user name changed? 
    String user = request.getParameter("user");
    if (user != null) {
    	// special handling for GET-request, FORM-data always handled as ISO-8859-1
    	user = new String(user.getBytes("ISO-8859-1"), "UTF-8");
	}
    if ((user != null) && !user.equals (Config.getUserName())) {
        Config.setUserName (user);
    }
    user = Config.getUserName();
    
    // booking year changed?
    String bookingYear = request.getParameter ("year");
    if ((bookingYear != null) && !bookingYear.equals ("" + Config.getBookingYear())) {
        Config.setBookingYear (Integer.parseInt(bookingYear));
    }
    bookingYear = "" + Config.getBookingYear();    

    out.println ("<tr><td>UST_DATA</td><td>" + Config.getFileName("") + "</td></tr>");
    out.println ("<tr><td>UST_CURRENCY</td><td>" + Config.getCurrency() + "</td></tr>");
    out.println ("<tr><td>UST_TAX_NUMBER</td><td>" + Config.getTaxNumber() + "</td></tr>");
    out.println ("<tr><td>user.name</td><td><input name=\"user\" size=20 maxlength=40 value=\"" + user + "\" title=\"Voreinstellung für den Bearbeiter neu angelegter oder geänderter Einträge\"></td></tr>");
    
%>
<tr><td>Monat</td><td> 
<!-- dynamic include -->
<% String pageUrl = "MonthChoosen.jsp?selectValue=" + month; %>
<jsp:include page="<%= pageUrl %>" flush="true"></jsp:include>
</td></tr>
<%
    
    out.println ("<tr><td>Jahr</td><td><input name=\"year\" size=4 maxlength=4 value=\"" + bookingYear + "\" title=\"Voreinstellung für die Jahreszahl in den Überschriften\"></td></tr>");

    out.println ("</table><br><br>");

    session.setAttribute ("month", (new Integer (month)).toString());

    out.println ("<input type=submit value=Übernehmen title=\"die konfigurierten Werte übernehmen\">");
    out.println ("</form>");
}
catch (Throwable t) {
    out.println ("</TABLE><P><FONT COLOR=RED>Es wurde eine Ausnahme " + t + " geworfen: " + t.getMessage() + "</FONT></P>");
}
%>
</center>
</body>
</html>
