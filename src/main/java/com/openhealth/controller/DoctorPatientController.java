package com.openhealth.controller;

import com.openhealth.model.PatientShare;
import com.openhealth.security.CurrentDoctor;
import com.openhealth.service.ProntuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctor")
public class DoctorPatientController {

    private final ProntuarioService service;

    public DoctorPatientController(ProntuarioService service) {
        this.service = service;
    }

    @GetMapping("/shares")
    public List<Map<String, Object>> incoming(HttpServletRequest req) {
        return service.incomingForDoctor(CurrentDoctor.requireId(req));
    }

    @GetMapping("/patients/{id}/prontuario")
    public ResponseEntity<Map<String, Object>> prontuario(@PathVariable Long id, HttpServletRequest req) {
        Long doctorId = CurrentDoctor.requireId(req);
        Optional<PatientShare> shareOpt = service.findShare(id, doctorId);
        if (shareOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Você não tem autorização do paciente para ver este prontuário."));
        }
        return ResponseEntity.ok(service.buildProntuario(shareOpt.get()));
    }
}