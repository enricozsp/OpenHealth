package com.openhealth.controller;

import com.openhealth.model.Consultation;
import com.openhealth.repository.ConsultationRepository;
import com.openhealth.security.CurrentUser;
import com.openhealth.service.OwnedResourceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final OwnedResourceService<Consultation> service;

    public ConsultationController(ConsultationRepository repo) {
        this.service = new OwnedResourceService<>(repo);
    }

    @GetMapping
    public List<Consultation> list(HttpServletRequest req) {
        return service.listFor(CurrentUser.requireId(req));
    }

    @PostMapping
    public Consultation create(@Valid @RequestBody Consultation body, HttpServletRequest req) {
        return service.create(body, CurrentUser.requireId(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consultation> update(@PathVariable Long id, @Valid @RequestBody Consultation body, HttpServletRequest req) {
        return service.update(id, body, CurrentUser.requireId(req))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest req) {
        return service.delete(id, CurrentUser.requireId(req))
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}