package com.openhealth.controller;

import com.openhealth.model.Profile;
import com.openhealth.security.CurrentUser;
import com.openhealth.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping
    public Profile current(HttpServletRequest req) {
        return service.loadOrThrow(CurrentUser.requireId(req));
    }

    @PutMapping
    public ResponseEntity<Profile> update(@Valid @RequestBody Profile body, HttpServletRequest req) {
        return ResponseEntity.ok(service.update(CurrentUser.requireId(req), body));
    }
}