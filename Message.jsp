<!--
  ust - my VAT calculating project
  Message.jsp - show a green message if session attribute message set
  hlu, May 12 2001 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
-->

<%@ page import="de.hlu.ust.*" %>
<%@ page pageEncoding="UTF-8" %>

<%
try {
    String msg = (String)session.getAttribute ("message");

    if ((msg != null)) {
        out.println ("<P><FONT COLOR=DARKGREEN>Hinweis: " + msg + "</FONT></P>");
        session.removeAttribute ("message");
    }

    msg = (String)session.getAttribute ("warning");

    if ((msg != null)) {
        out.println ("<P><FONT COLOR=BROWN>Warnung: " + msg + "</FONT></P>");
        session.removeAttribute ("warning");
    }
    
    msg = (String)session.getAttribute ("error");

    if ((msg != null)) {
        out.println ("<P><FONT COLOR=RED>Fehler: " + msg + "</FONT></P>");
        session.removeAttribute ("error");
    }
    
}
catch (Throwable t) {
    out.println ("</TABLE><P><FONT COLOR=RED>Es wurde eine Ausnahme " + t + " geworfen: " + t.getMessage() + "</FONT></P>");
}
%>
