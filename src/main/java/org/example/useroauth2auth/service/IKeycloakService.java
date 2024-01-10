package org.example.useroauth2auth.service;

import org.example.useroauth2auth.controller.DTO.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;
import java.util.List;

public interface IKeycloakService {
    List<UserRepresentation> findAllUsers();
    List<UserRepresentation> searchUserByUsername(String username);
    String createUser(UserDTO user);
    void deleteUser(String id);
    void updateUser(String id, UserDTO user);
}
