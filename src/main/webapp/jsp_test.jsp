<%@ page import="javax.faces.context.FacesContext" %><%--
  Created by IntelliJ IDEA.
  User: zhang
  Date: 10/26/2020
  Time: 22:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>GreatThings</title>
</head>
<body>
    <h1>GreatThings</h1>
    <a href="base">Base</a>
    <a href="home">Home</a>
<%--    Scriptlet version, DO NOT USE: --%>
    <%
        double num = Math.random();
        if(num > 0.5){
    %>
        <h2>Gooooooooooooooood</h2>
        <p><%=num%></p>
    <%
        } else {
    %>
        <h2>Bad</h2>
        <p><%=num%></p>
    <%
        }
    %>
    <%System.out.println("JSF Version: " + FacesContext.class.getPackage().getImplementationVersion()); %>
</body>
</html>
