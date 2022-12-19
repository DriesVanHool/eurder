package com.switchfully.eurder.domain.security;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

public class KeyCloakConfig {
    static Keycloak keycloak = null;
    final static String serverUrl = "https://keycloak.switchfully.com/auth/";
    public final static String realm = "java-oct-2022";
    final static String clientId = "eurder-dries";
    final static String clientSecret = "HhEMkchwPljhhzs6osa4nlb8GWiZiJoE";
    final static String userName =  "admin";
    final static String password = "pwd";

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
}
