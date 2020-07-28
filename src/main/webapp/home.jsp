<%--
  Created by IntelliJ IDEA.
  User: chamaths
  Date: 7/27/20
  Time: 21:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import ="java.util.*" %>
<html>
<head>
    <title>Home</title>
    <style>
        html, body {
            height: 100%;
        }
        body {
            flex-direction: column;
            display: flex;
        }
        main {
            flex-shrink: 0;
        }
        main.center-segment {
            margin: auto;
            display: flex;
            align-items: center;
        }
        .element-padding {
            margin: auto;
            padding: 15px;
        }
    </style>
</head>
<body>
<main class="center-segment">
    <%
    String userName = (String) request.getAttribute("username");
    String password = (String) request.getAttribute("password");
    out.println("<br>User name: " + userName + "<br>");
        out.println("<br>Password: " + password + "<br>");
    %>
</main>
</body>
</html>
