/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.sample;

import org.wso2.carbon.identity.sso.agent.saml.bean.LoggedInSessionBean;
import org.wso2.carbon.identity.sso.agent.saml.bean.SSOAgentConfig;
import org.wso2.carbon.identity.sso.agent.saml.util.SSOAgentConstants;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SampleSSOFilter implements Filter {

    protected FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {

        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if(request.getSession(false) != null &&
                request.getSession(false).getAttribute(SSOAgentConstants.SESSION_BEAN_NAME) == null){
            request.getSession().invalidate();
//            ((HttpServletResponse) servletResponse).sendRedirect("samlsso?SAML2.HTTPBinding=HTTP-POST");
        }
        LoggedInSessionBean sessionBean =
                (LoggedInSessionBean)request.getSession().getAttribute(SSOAgentConstants.SESSION_BEAN_NAME);

        if(sessionBean != null){
            if(sessionBean.getSAML2SSO() == null) {
                request.getRequestDispatcher("samlsso?SAML2.HTTPBinding=HTTP-POST").forward(request, response);
                return;
            }
        } else {
            ((HttpServletResponse) servletResponse).sendRedirect("samlsso?SAML2.HTTPBinding=HTTP-POST");
            return;
        }

        String httpBinding = servletRequest.getParameter(
                SSOAgentConstants.SSOAgentConfig.SAML2.HTTP_BINDING);
        if(httpBinding != null && !httpBinding.isEmpty()){
            if("HTTP-POST".equals(httpBinding)){
                httpBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
            } else if ("HTTP-Redirect".equals(httpBinding)) {
                httpBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect";
            } else {
                //default
                httpBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
            }
        } else {
            //default
            httpBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
        }
        SSOAgentConfig
                config = (SSOAgentConfig)filterConfig.getServletContext().getAttribute(SSOAgentConstants.CONFIG_BEAN_NAME);
        config.getSAML2().setHttpBinding(httpBinding);
        config.getSAML2().setPostBindingRequestHTMLPayload(null);
        servletRequest.setAttribute(SSOAgentConstants.CONFIG_BEAN_NAME,config);

        chain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {

    }
}
