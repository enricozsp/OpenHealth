package com.openhealth.controller;

import com.openhealth.dto.LoginRequest;
import com.openhealth.dto.RegisterRequest;
import com.openhealth.model.Profile;
import com.openhealth.security.SessionManager;
import com.openhealth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final SessionManager sessions;

    public AuthController(AuthService authService, SessionManager sessions) {
        this.authService = authService;
        this.sessions = sessions;
    }

    @GetMapping("/status")
    public Map<String, Object> status(HttpServletRequest req) {
        return Map.of("authenticated", sessions.isPatientAuthenticated(req));
    }

    @GetMapping("/me")
    public ResponseEntity<Profile> me(HttpServletRequest req) {
        if (!sessions.isPatientAuthenticated(req)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return authService.findById(currentId(req))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest body, HttpServletRequest req) {
        if (authService.emailExists(body.getEmail().trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email já cadastrado."));
        }
        Profile saved = authService.register(body);
        sessions.loginAsPatient(req, saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest body, HttpServletRequest req) {
        Optional<Profile> opt = authService.authenticate(body);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Email ou senha inválidos."));
        }
        Profile p = opt.get();
        sessions.loginAsPatient(req, p.getId());
        return ResponseEntity.ok(p);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req) {
        sessions.logout(req);
        return ResponseEntity.noContent().build();
    }

    private Long currentId(HttpServletRequest req) {
        return com.openhealth.security.CurrentUser.requireId(req);
    }
}