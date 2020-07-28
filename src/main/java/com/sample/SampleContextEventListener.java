package com.sample;

import org.wso2.carbon.identity.sso.agent.bean.SSOAgentConfig;
import org.wso2.carbon.identity.sso.agent.exception.SSOAgentException;
import org.wso2.carbon.identity.sso.agent.security.SSOAgentX509Credential;
import org.wso2.carbon.identity.sso.agent.security.SSOAgentX509KeyStoreCredential;
import org.wso2.carbon.identity.sso.agent.util.SSOAgentConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SampleContextEventListener implements ServletContextListener {

    private static Properties properties;

    public void contextInitialized(ServletContextEvent servletContextEvent) {

        properties = new Properties();
        try {
            properties.load(servletContextEvent.getServletContext().
                    getResourceAsStream("/WEB-INF/classes/sampleApp.properties"));

            InputStream keyStoreInputStream = servletContextEvent.getServletContext().
                    getResourceAsStream("/WEB-INF/classes/wso2carbon.jks");
            SSOAgentX509Credential credential =
                    new SSOAgentX509KeyStoreCredential(keyStoreInputStream,
                            properties.getProperty("KeyStorePassword").toCharArray(),
                            properties.getProperty("IdPPublicCertAlias"),
                            properties.getProperty("PrivateKeyAlias"),
                            properties.getProperty("PrivateKeyPassword").toCharArray());

            SSOAgentConfig config = new SSOAgentConfig();
            config.initConfig(properties);
            config.getSAML2().setSSOAgentX509Credential(credential);
            servletContextEvent.getServletContext().setAttribute(SSOAgentConstants.CONFIG_BEAN_NAME, config);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SSOAgentException e) {
            e.printStackTrace();
        }

    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    /**
     * Get the properties of the sample
     * @return Properties
     */
    public static Properties getProperties(){
        return properties;
    }
}
