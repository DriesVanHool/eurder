package com.switchfully.eurder.domain.security;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class KeyCloakConfig {
    static Properties prop;

    static {
        try {
            prop = readPropertiesFile("env.properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Keycloak keycloak = null;
    final static String serverUrl = prop.getProperty("KEYCLOAK_SERVER");
    public final static String realm = prop.getProperty("KEYCLOAK_REALM");
    final static String clientId =  prop.getProperty("KEYCLOAK_CLIENT");
    private static String clientSecret = prop.getProperty("KEYCLOAK_SECRET");
    final static String userName =  prop.getProperty("ADMIN_NAME");
    final static String password = prop.getProperty("ADMIN_PWD");



    public static Keycloak getInstance(){
        if(keycloak == null){

            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(userName)
                    .password(password)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .resteasyClient(new ResteasyClientBuilder()
                            .connectionPoolSize(10)
                            .build()
                    )
                    .build();
        }
        return keycloak;
    }

    public static Properties readPropertiesFile(String fileName) throws IOException {
        FileInputStream fis = null;
        Properties prop = null;
        try {
            fis = new FileInputStream(fileName);
            prop = new Properties();
            prop.load(fis);
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            fis.close();
        }
        return prop;
    }
}
