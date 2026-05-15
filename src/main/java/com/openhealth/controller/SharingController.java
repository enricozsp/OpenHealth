package com.openhealth.controller;

import com.openhealth.dto.SharingSettings;
import com.openhealth.security.CurrentUser;
import com.openhealth.service.SharingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sharing")
public class SharingController {

    private final SharingService service;

    public SharingController(SharingService service) {
        this.service = service;
    }

    @GetMapping
    public List<Map<String, Object>> mine(HttpServletRequest req) {
        return service.sharesOfPatient(CurrentUser.requireId(req));
    }

    @GetMapping("/doctors")
    public List<Map<String, Object>> availableDoctors(HttpServletRequest req) {
        CurrentUser.requireId(req);
        return service.listAllDoctors();
    }

    @PostMapping
    public ResponseEntity<?> upsert(@RequestBody SharingSettings body, HttpServletRequest req) {
        Long patientId = CurrentUser.requireId(req);
        if (body == null || body.getDoctorId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Selecione um médico."));
        }
        if (service.findDoctor(body.getDoctorId()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Médico não encontrado."));
        }
        service.upsert(patientId, body);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @DeleteMapping("/{doctorId}")
    public ResponseEntity<Void> revoke(@PathVariable Long doctorId, HttpServletRequest req) {
        service.revoke(CurrentUser.requireId(req), doctorId);
        return ResponseEntity.noContent().build();
    }
}