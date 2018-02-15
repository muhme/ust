<!--
  ust - my VAT calculating project
  index.jsp - the first entry point
  hlu, May 12 2001 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
-->

<%@ page session="true" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>

<HTML>
<HEAD>
    <TITLE>ust</TITLE>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="formate.css">
</head>
<body>

<%@ include file="Menu.jsp" %>
<jsp:include page="Message.jsp" flush="true" />
<center>
<H1>ust</H1>
<H2>Version <%= Finance.VERSION %></H2>
<b><%= Finance.DATE %></b>
<br>
<br>
<br>
<img src=money.jpg>
</center>
</BODY>

</HTML>
