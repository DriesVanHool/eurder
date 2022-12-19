package com.switchfully.eurder.services;

import com.switchfully.eurder.api.dtos.CreateUserDto;
import com.switchfully.eurder.domain.exceptions.KeyCloakCreateException;
import com.switchfully.eurder.domain.security.KeyCloakConfig;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class KeyCloakService {
    public void addUser(CreateUserDto createUserDto) throws KeyCloakCreateException {
        UsersResource usersResource = KeyCloakConfig.getInstance().realm(KeyCloakConfig.realm).users();
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(createUserDto.password());

        UserRepresentation keyCloakUser = new UserRepresentation();
        keyCloakUser.setUsername(createUserDto.email());
        keyCloakUser.setCredentials(Collections.singletonList(credentialRepresentation));
        keyCloakUser.setFirstName(createUserDto.firstname());
        keyCloakUser.setLastName(createUserDto.lastname());
        keyCloakUser.setEmail(createUserDto.email());
        keyCloakUser.setEnabled(true);
        keyCloakUser.setEmailVerified(false);
        usersResource.create(keyCloakUser);
/*        if (usersResource.create(keyCloakUser).getStatus() != 201) {
            throw new KeyCloakCreateException();
        }*/
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }
}
