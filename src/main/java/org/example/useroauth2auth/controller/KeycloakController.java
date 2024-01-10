package org.example.useroauth2auth.controller;

import lombok.AllArgsConstructor;
import org.example.useroauth2auth.controller.DTO.UserDTO;
import org.example.useroauth2auth.service.IKeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/keycloak/user")
@PreAuthorize("hasRole('admin_client_role')")
@AllArgsConstructor
public class KeycloakController {

    private final IKeycloakService service;

    @GetMapping("/search")
    public ResponseEntity<?> findAllUsers(){return ResponseEntity.ok(service.findAllUsers());}

    @GetMapping("/search/{username}")
    public ResponseEntity<?> findByUsername(@PathVariable String username){
        return ResponseEntity.ok(service.searchUserByUsername(username));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO)throws URISyntaxException {
        String response = service.createUser(userDTO);
        return ResponseEntity.created(new URI("/keycloak/user/create")).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id,@RequestBody UserDTO userDTO){
        service.updateUser(id,userDTO);
        return ResponseEntity.ok("User updated successfully!!");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
