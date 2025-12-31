<!--
  index.jsp - the first entry point

  ust web application, Copyright (c) 2001 Heiko Lübbe, MIT License, https://github.com/muhme/ust
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
