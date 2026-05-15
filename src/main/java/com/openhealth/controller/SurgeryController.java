package com.openhealth.controller;

import com.openhealth.model.Surgery;
import com.openhealth.repository.SurgeryRepository;
import com.openhealth.security.CurrentUser;
import com.openhealth.service.OwnedResourceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/surgeries")
public class SurgeryController {

    private final OwnedResourceService<Surgery> service;

    public SurgeryController(SurgeryRepository repo) {
        this.service = new OwnedResourceService<>(repo);
    }

    @GetMapping
    public List<Surgery> list(HttpServletRequest req) {
        return service.listFor(CurrentUser.requireId(req));
    }

    @PostMapping
    public Surgery create(@Valid @RequestBody Surgery body, HttpServletRequest req) {
        return service.create(body, CurrentUser.requireId(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Surgery> update(@PathVariable Long id, @Valid @RequestBody Surgery body, HttpServletRequest req) {
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