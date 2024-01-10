package org.example.useroauth2auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class principalController {
    @GetMapping("/admin")
    @PreAuthorize("hasRole('admin_client_role')")
    public String helloAdmin() {
        return "Hello Admin";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('user_client_role') or hasRole('admin_client_role')")
    public String helloUser() {
        return "Hello User";
    }
}
