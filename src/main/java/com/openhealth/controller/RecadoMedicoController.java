package com.openhealth.controller;

import com.openhealth.dto.RecadoMedicoCreateRequest;
import com.openhealth.dto.RecadoMedicoResponse;
import com.openhealth.dto.RecadoMedicoUpdateRequest;
import com.openhealth.security.CurrentDoctor;
import com.openhealth.security.CurrentUser;
import com.openhealth.security.SessionKeys;
import com.openhealth.service.RecadoMedicoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recados-medicos")
public class RecadoMedicoController {

    private final RecadoMedicoService service;

    public RecadoMedicoController(RecadoMedicoService service) {
        this.service = service;
    }

    // POST /api/recados-medicos — médico autenticado cria um recado.
    @PostMapping
    public ResponseEntity<RecadoMedicoResponse> criar(@Valid @RequestBody RecadoMedicoCreateRequest body,
                                                      HttpServletRequest req) {
        Long medicoSessaoId = CurrentDoctor.requireId(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(body, medicoSessaoId));
    }

    // GET /api/recados-medicos/paciente/{pacienteId} — paciente lista os seus,
    // ou médico autenticado lista os de um paciente que compartilhou com ele.
    @GetMapping("/paciente/{pacienteId}")
    public List<RecadoMedicoResponse> listarDoPaciente(@PathVariable Long pacienteId,
                                                       HttpServletRequest req) {
        Long pacienteSessaoId = userIdOpcional(req);
        Long medicoSessaoId = doctorIdOpcional(req);
        return service.listarDoPaciente(pacienteId, pacienteSessaoId, medicoSessaoId);
    }

    // PATCH /api/recados-medicos/{recadoId}/lido — paciente autenticado marca como lido.
    @PatchMapping("/{recadoId}/lido")
    public RecadoMedicoResponse marcarComoLido(@PathVariable Long recadoId,
                                               HttpServletRequest req) {
        Long pacienteSessaoId = CurrentUser.requireId(req);
        return service.marcarComoLido(recadoId, pacienteSessaoId);
    }

    // PUT /api/recados-medicos/{recadoId} — médico autor edita.
    @PutMapping("/{recadoId}")
    public RecadoMedicoResponse editar(@PathVariable Long recadoId,
                                       @Valid @RequestBody RecadoMedicoUpdateRequest body,
                                       HttpServletRequest req) {
        Long medicoSessaoId = CurrentDoctor.requireId(req);
        return service.editar(recadoId, body, medicoSessaoId);
    }

    // DELETE /api/recados-medicos/{recadoId} — médico autor exclui.
    @DeleteMapping("/{recadoId}")
    public ResponseEntity<Void> excluir(@PathVariable Long recadoId, HttpServletRequest req) {
        Long medicoSessaoId = CurrentDoctor.requireId(req);
        service.excluir(recadoId, medicoSessaoId);
        return ResponseEntity.noContent().build();
    }

    // GET /api/recados-medicos/medico/{medicoId}/paciente/{pacienteId} — histórico do médico autor.
    @GetMapping("/medico/{medicoId}/paciente/{pacienteId}")
    public List<RecadoMedicoResponse> historico(@PathVariable Long medicoId,
                                                @PathVariable Long pacienteId,
                                                HttpServletRequest req) {
        Long medicoSessaoId = CurrentDoctor.requireId(req);
        return service.historicoDoMedicoParaPaciente(medicoId, pacienteId, medicoSessaoId);
    }

    private static Long userIdOpcional(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (Long) session.getAttribute(SessionKeys.USER_ID);
    }

    private static Long doctorIdOpcional(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (Long) session.getAttribute(SessionKeys.DOCTOR_ID);
    }
}