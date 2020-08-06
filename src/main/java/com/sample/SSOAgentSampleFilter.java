package com.sample;

import org.opensaml.saml.saml2.core.LogoutResponse;
import org.wso2.carbon.identity.sso.agent.saml.SAML2SSOManager;
import org.wso2.carbon.identity.sso.agent.saml.bean.SSOAgentConfig;
import org.wso2.carbon.identity.sso.agent.saml.exception.SSOAgentException;
import org.wso2.carbon.identity.sso.agent.saml.util.SSOAgentConstants;
import org.wso2.carbon.identity.sso.agent.saml.util.SSOAgentFilterUtils;
import org.wso2.carbon.identity.sso.agent.saml.util.SSOAgentRequestResolver;
import org.wso2.carbon.identity.sso.agent.saml.util.SSOAgentUtils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SSOAgentSampleFilter implements Filter {

    protected FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {

        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        SSOAgentConfig ssoAgentConfig = SSOAgentFilterUtils.getSSOAgentConfig(filterConfig);

        SSOAgentRequestResolver resolver =
                new SSOAgentRequestResolver(request, response, ssoAgentConfig);

        if (resolver.isURLToSkip()) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        SAML2SSOManager samlSSOManager;

        if (resolver.isSAML2SSOResponse()) {
            samlSSOManager = new SAML2SSOManager(ssoAgentConfig);
            try {
                samlSSOManager.processResponse(request, response);
            } catch (SSOAgentException e) {
                handleException(request, e);
            }
        }
        else if (resolver.isSLORequest()) {
            samlSSOManager = new SAML2SSOManager(ssoAgentConfig);
            LogoutResponse logoutResponse = samlSSOManager.doSLO(request);
            String encodedRequestMessage = samlSSOManager.buildPostResponse(logoutResponse);
            SSOAgentUtils.sendPostResponse(request, response, encodedRequestMessage);
            return;
        }
        else if (resolver.isSAML2SSOURL()) {
            samlSSOManager = new SAML2SSOManager(ssoAgentConfig);
            if (resolver.isHttpPostBinding()) {
                String htmlPayload = samlSSOManager.buildPostRequest(request, response, false);
                SSOAgentUtils.sendPostResponse(request, response, htmlPayload);
                return;
            }
            response.sendRedirect(samlSSOManager.buildRedirectRequest(request, false));
            return;
        }  else if (resolver.isSLOURL()) {
            samlSSOManager = new SAML2SSOManager(ssoAgentConfig);
            if (resolver.isHttpPostBinding()) {
                boolean isPassiveAuth = ssoAgentConfig.getSAML2().isPassiveAuthn();
                ssoAgentConfig.getSAML2().setPassiveAuthn(false);
                String htmlPayload = samlSSOManager.buildPostRequest(request, response, true);
                ssoAgentConfig.getSAML2().setPassiveAuthn(isPassiveAuth);
                SSOAgentUtils.sendPostResponse(request, response, htmlPayload);
            } else {
                //if "SSOAgentConstants.HTTP_BINDING_PARAM" is not defined, default to redirect
                boolean isPassiveAuth = ssoAgentConfig.getSAML2().isPassiveAuthn();
                ssoAgentConfig.getSAML2().setPassiveAuthn(false);
                String redirectUrl = samlSSOManager.buildRedirectRequest(request, true);
                ssoAgentConfig.getSAML2().setPassiveAuthn(isPassiveAuth);
                response.sendRedirect(redirectUrl);
            }
            return;
        }
        filterChain.doFilter(request, response);
    }

    public void destroy() {

        return;
    }

    protected void handleException(HttpServletRequest request, SSOAgentException e)
            throws SSOAgentException {

        if (request.getSession(false) != null) {
            request.getSession(false).removeAttribute(SSOAgentConstants.SESSION_BEAN_NAME);
        }
        throw e;
    }
}
