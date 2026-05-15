package com.openhealth.controller;

import com.openhealth.model.Allergy;
import com.openhealth.repository.AllergyRepository;
import com.openhealth.security.CurrentUser;
import com.openhealth.service.OwnedResourceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/allergies")
public class AllergyController {

    private final OwnedResourceService<Allergy> service;

    public AllergyController(AllergyRepository repo) {
        this.service = new OwnedResourceService<>(repo);
    }

    @GetMapping
    public List<Allergy> list(HttpServletRequest req) {
        return service.listFor(CurrentUser.requireId(req));
    }

    @PostMapping
    public Allergy create(@Valid @RequestBody Allergy body, HttpServletRequest req) {
        return service.create(body, CurrentUser.requireId(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Allergy> update(@PathVariable Long id, @Valid @RequestBody Allergy body, HttpServletRequest req) {
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