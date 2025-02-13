package com.atm.controller.api;

import com.atm.model.entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/atm/api")
public class UserRoleExposerApi {

    @GetMapping("/user-role")
    public ResponseEntity<?> getUserRole
            (Principal principal) {
        Authentication authentication = (Authentication) principal;
        Map<String, String> response = new HashMap<>();
        AtomicReference<String> role =
                new AtomicReference<>("");
        authentication.getAuthorities().forEach(au -> {
            role.set(au.getAuthority());
        });
        response.put("role", role.get());
        return ResponseEntity.ok(response);
    }

}
