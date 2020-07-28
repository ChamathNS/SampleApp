package com.sample;

import org.apache.axiom.om.util.Base64;
import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.identity.sso.agent.SAML2SSOAgentFilter;
import org.wso2.carbon.identity.sso.agent.bean.SSOAgentConfig;
import org.wso2.carbon.identity.sso.agent.util.SSOAgentConstants;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class SSOAgentSampleFilter extends SAML2SSOAgentFilter {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String CHARACTER_ENCODING = "UTF-8";
    private static Properties properties;

    static{
        properties = SampleContextEventListener.getProperties();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        String httpBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
        SSOAgentConfig config = (SSOAgentConfig)filterConfig.getServletContext().getAttribute(
                SSOAgentConstants.CONFIG_BEAN_NAME);
        config.getSAML2().setHttpBinding(httpBinding);

        if (StringUtils.isNotEmpty(servletRequest.getParameter(USERNAME)) &&
                StringUtils.isNotEmpty(servletRequest.getParameter(PASSWORD))) {
            String authorization = servletRequest.getParameter(USERNAME) + ":" + servletRequest.getParameter(PASSWORD);
            authorization = Base64.encode(authorization.getBytes(CHARACTER_ENCODING));
            String htmlPayload = "<html>\n" +
                    "<body>\n" +
                    "<p>You are now redirected back to " + properties.getProperty("SAML2.IdPURL") + " \n" +
                    "If the redirection fails, please click the post button.</p>\n" +
                    "<form method='post' action='" +  properties.getProperty("SAML2.IdPURL") + "'>\n" +
                    "<input type='hidden' name='sectoken' value='" + authorization + "'/>\n" +
                    "<p>\n" +
                    "<!--$saml_params-->\n" +
                    "<button type='submit'>POST</button>\n" +
                    "</p>\n" +
                    "</form>\n" +
                    "<script type='text/javascript'>\n" +
                    "document.forms[0].submit();\n" +
                    "</script>\n" +
                    "</body>\n" +
                    "</html>";
            config.getSAML2().setPostBindingRequestHTMLPayload(htmlPayload);
        } else {
            config.getSAML2().setPostBindingRequestHTMLPayload(null);
        }
        servletRequest.setAttribute(SSOAgentConstants.CONFIG_BEAN_NAME,config);
        super.doFilter(servletRequest, servletResponse, filterChain);
    }
}
