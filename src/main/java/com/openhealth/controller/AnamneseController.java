package com.openhealth.controller;

import com.openhealth.model.Anamnese;
import com.openhealth.security.CurrentUser;
import com.openhealth.service.AnamneseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/anamnese")
public class AnamneseController {

    private final AnamneseService service;

    public AnamneseController(AnamneseService service) {
        this.service = service;
    }

    @GetMapping
    public Anamnese current(HttpServletRequest req) {
        return service.loadOrCreate(CurrentUser.requireId(req));
    }

    @PutMapping
    public ResponseEntity<Anamnese> update(@RequestBody Anamnese body, HttpServletRequest req) {
        return ResponseEntity.ok(service.update(CurrentUser.requireId(req), body));
    }
}