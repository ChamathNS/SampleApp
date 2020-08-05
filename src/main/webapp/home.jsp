<%--
  Created by IntelliJ IDEA.
  User: chamaths
  Date: 7/27/20
  Time: 21:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="java.util.Map" %>
<%@ page import="org.wso2.carbon.identity.sso.agent.saml.bean.LoggedInSessionBean" %>
<%@ page import="org.wso2.carbon.identity.sso.agent.saml.util.SSOAgentConstants" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
<%
    String subjectId = null;
    Map<String, String> saml2SSOAttributes = null;
    if(request.getSession(false) != null &&
            request.getSession(false).getAttribute(SSOAgentConstants.SESSION_BEAN_NAME) == null){
        request.getSession().invalidate();
%>
<script type="text/javascript">
    location.href = "index.html";
</script>
<%
        return;
    }
    LoggedInSessionBean sessionBean = (LoggedInSessionBean)session.getAttribute(SSOAgentConstants.SESSION_BEAN_NAME);
    
    if(sessionBean != null){
        if(sessionBean.getSAML2SSO() != null) {
            subjectId = sessionBean.getSAML2SSO().getSubjectId();
            saml2SSOAttributes = sessionBean.getSAML2SSO().getSubjectAttributes();
        } else {
%>
<script type="text/javascript">
    location.href = "index.html";
</script>
<%
        return;
    }
} else {
%>
<script type="text/javascript">
    location.href = "index.html";
</script>
<%
        return;
    }
%>
<body>
<main class="center-segment">
    <div style="text-align: center">
        <div class="element-padding">
            <h1>Home Page!</h1>
        </div>
        <div class="element-padding">
            <%
                if(subjectId != null){
            %>
            <p> You are logged in as <%=subjectId%></p>
            <%
                }
            %>
        </div>
        <div class="element-padding">
            <table>
                <%
                    if(saml2SSOAttributes != null){
                        for (Map.Entry<String, String> entry:saml2SSOAttributes.entrySet()) {
                %>
                            <tr>
                                <td><%=entry.getKey()%></td>
                                <td><%=entry.getValue()%></td>
                            </tr>
                <%
                    }
                }
                %>
            </table>
        </div>
        <div class="element-padding">
            <a href="logout?SAML2.HTTPBinding=HTTP-POST">Logout</a>
        </div>
    </div>
</main>
</body>
</html>
