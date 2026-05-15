package com.openhealth.controller;

import com.openhealth.model.ClinicalExam;
import com.openhealth.repository.ClinicalExamRepository;
import com.openhealth.security.CurrentUser;
import com.openhealth.service.OwnedResourceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ClinicalExamController {

    private final OwnedResourceService<ClinicalExam> service;

    public ClinicalExamController(ClinicalExamRepository repo) {
        this.service = new OwnedResourceService<>(repo);
    }

    @GetMapping
    public List<ClinicalExam> list(HttpServletRequest req) {
        return service.listFor(CurrentUser.requireId(req));
    }

    @PostMapping
    public ClinicalExam create(@Valid @RequestBody ClinicalExam body, HttpServletRequest req) {
        return service.create(body, CurrentUser.requireId(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClinicalExam> update(@PathVariable Long id, @Valid @RequestBody ClinicalExam body, HttpServletRequest req) {
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