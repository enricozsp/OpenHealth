package com.openhealth.controller;

import com.openhealth.dto.DoctorRegisterRequest;
import com.openhealth.dto.LoginRequest;
import com.openhealth.model.Doctor;
import com.openhealth.security.CurrentDoctor;
import com.openhealth.security.SessionManager;
import com.openhealth.service.DoctorAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctor-auth")
public class DoctorAuthController {

    private final DoctorAuthService service;
    private final SessionManager sessions;

    public DoctorAuthController(DoctorAuthService service, SessionManager sessions) {
        this.service = service;
        this.sessions = sessions;
    }

    @GetMapping("/status")
    public Map<String, Object> status(HttpServletRequest req) {
        return Map.of("authenticated", sessions.isDoctorAuthenticated(req));
    }

    @GetMapping("/me")
    public ResponseEntity<Doctor> me(HttpServletRequest req) {
        if (!sessions.isDoctorAuthenticated(req)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return service.findById(CurrentDoctor.requireId(req))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody DoctorRegisterRequest body, HttpServletRequest req) {
        if (service.emailExists(body.getEmail().trim().toLowerCase())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email já cadastrado."));
        }
        if (service.crmExists(body.getCrm().trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "CRM já cadastrado."));
        }
        Doctor saved = service.register(body);
        sessions.loginAsDoctor(req, saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest body, HttpServletRequest req) {
        Optional<Doctor> opt = service.authenticate(body);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Email ou senha inválidos."));
        }
        Doctor d = opt.get();
        sessions.loginAsDoctor(req, d.getId());
        return ResponseEntity.ok(d);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req) {
        sessions.logout(req);
        return ResponseEntity.noContent().build();
    }
}