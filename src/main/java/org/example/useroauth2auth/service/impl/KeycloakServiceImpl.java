package org.example.useroauth2auth.service.impl;

import jakarta.ws.rs.core.Response;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.example.useroauth2auth.controller.DTO.UserDTO;
import org.example.useroauth2auth.service.IKeycloakService;
import org.example.useroauth2auth.utils.KeycloakProvider;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KeycloakServiceImpl implements IKeycloakService {
    @Override
    public List<UserRepresentation> findAllUsers() {return KeycloakProvider.getRealmResource().users().list();}

    @Override
    public List<UserRepresentation> searchUserByUsername(String username) {
        return KeycloakProvider.getRealmResource().users().searchByUsername(username, true);
    }

    @Override
    public String createUser(@NonNull UserDTO user) {
        int status = 0;
        UsersResource userResource = (UsersResource) KeycloakProvider.getUsersResource();

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);

        Response response = userResource.create(userRepresentation);
        status = response.getStatus();

        if (status == 201){
            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf("/") + 1);

            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType(OAuth2Constants.PASSWORD);
            credentialRepresentation.setValue(user.getPassword());

            userResource.get(userId).resetPassword(credentialRepresentation);

            RealmResource realmResource = KeycloakProvider.getRealmResource();

            List<RoleRepresentation> roleRepresentations = null;

            if(user.getRoles() == null || user.getRoles().isEmpty()){
                roleRepresentations = List.of(realmResource.roles().get("user").toRepresentation());
            }else{
                roleRepresentations = realmResource.roles()
                        .list()
                        .stream()
                        .filter(role -> user.getRoles()
                                .stream()
                                .anyMatch(roleName -> roleName.equalsIgnoreCase(role.getName())))
                        .toList();
            }
            realmResource.users()
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .add(roleRepresentations);

            return "User created successfully";
            
        } else if (status == 409) {
            log.warn("User exist already!!");
            return "User exist already";
        } else {
            return "Error creating user, please contact the administrator";
        }
    }

    @Override
    public void deleteUser(String id) {
        KeycloakProvider.getUsersResource()
                .get(id).remove();
    }

    @Override
    public void updateUser(String id,@NonNull UserDTO user) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(OAuth2Constants.PASSWORD);
        credentialRepresentation.setValue(user.getPassword());

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        UserResource userResource = KeycloakProvider.getUsersResource().get(id);
        userResource.update(userRepresentation);
    }
}
