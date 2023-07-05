<!--
  ust - my VAT calculating project
  MonthChoosen.jsp - show all months and give a selection
  hlu, May 19 2001 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
-->

<%@ page import="de.hlu.ust.*" %>
<%@ page pageEncoding="UTF-8" %>

<%
try {

    // 1st param select name
    String selectName = request.getParameter ("selectName");

    if (selectName == null) {
        selectName = "month";
    }
    
    // 2nd param selected value
    int selectValue;    
    try {
        Integer i = new Integer (request.getParameter ("selectValue"));
        selectValue = i.intValue();
    }
    catch (Exception e) {
        // use first entry for all months
        selectValue = 0;
    }
    
    // 3rd param starting with all months (else starting with January)
    String startJanuary = request.getParameter ("startJanuary");
    int from = ( startJanuary != null ) ? 1 : 0;
    
    // 4th parameter only months or also quarter
    String withQuarters = request.getParameter ("withQuarters");
    int to = ( withQuarters != null ) ? Finance.months.length : Finance.months.length - 4;

    out.println ("<select name=\"" + selectName + "\" size=1 title=\"Monat auswählen\">");

    for (int i = from; i < to; ++i) {
        out.print ("<option value=" + i);
        if (i == selectValue) {
            out.print (" selected");
        }
        out.println (">" + Finance.months[i]);
    }
    out.println ("</select>");
}
catch (Throwable t) {
    out.println ("</TABLE><P><FONT COLOR=RED>Es wurde eine Ausnahme " + t + " geworfen: " + t.getMessage() + "</FONT></P>");
}

%>
