package com.openhealth.service;

import com.openhealth.dto.RecadoMedicoCreateRequest;
import com.openhealth.dto.RecadoMedicoResponse;
import com.openhealth.dto.RecadoMedicoUpdateRequest;
import com.openhealth.mapper.RecadoMedicoMapper;
import com.openhealth.model.Doctor;
import com.openhealth.model.PatientShare;
import com.openhealth.model.Profile;
import com.openhealth.model.RecadoMedico;
import com.openhealth.model.StatusRecado;
import com.openhealth.repository.DoctorRepository;
import com.openhealth.repository.PatientShareRepository;
import com.openhealth.repository.ProfileRepository;
import com.openhealth.repository.RecadoMedicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class RecadoMedicoService {

    private final RecadoMedicoRepository recadoRepo;
    private final ProfileRepository profileRepo;
    private final DoctorRepository doctorRepo;
    private final PatientShareRepository shareRepo;
    private final RecadoMedicoMapper mapper;

    public RecadoMedicoService(RecadoMedicoRepository recadoRepo,
                               ProfileRepository profileRepo,
                               DoctorRepository doctorRepo,
                               PatientShareRepository shareRepo,
                               RecadoMedicoMapper mapper) {
        this.recadoRepo = recadoRepo;
        this.profileRepo = profileRepo;
        this.doctorRepo = doctorRepo;
        this.shareRepo = shareRepo;
        this.mapper = mapper;
    }

    @Transactional
    public RecadoMedicoResponse criar(RecadoMedicoCreateRequest body, Long medicoSessaoId) {
        if (medicoSessaoId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Somente médicos autenticados podem criar recados.");
        }
        if (body == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido.");
        }
        if (body.getTitulo() == null || body.getTitulo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O título do recado é obrigatório.");
        }
        if (body.getMensagem() == null || body.getMensagem().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A mensagem do recado é obrigatória.");
        }
        if (body.getPacienteId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O paciente destinatário é obrigatório.");
        }
        // O medicoId do body, se vier, precisa ser o próprio médico autenticado.
        if (body.getMedicoId() != null && !medicoSessaoId.equals(body.getMedicoId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Médico autenticado não pode enviar recados em nome de outro médico.");
        }

        Doctor medico = doctorRepo.findById(medicoSessaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Médico não encontrado."));

        Profile paciente = profileRepo.findById(body.getPacienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado."));

        garantirPermissaoMedicoSobrePaciente(medico.getId(), paciente.getId());

        RecadoMedico recado = new RecadoMedico();
        recado.setTitulo(body.getTitulo().trim());
        recado.setMensagem(body.getMensagem().trim());
        recado.setDataCriacao(Instant.now());
        recado.setStatus(StatusRecado.NAO_LIDO);
        recado.setMedico(medico);
        recado.setPaciente(paciente);

        return mapper.toResponse(recadoRepo.save(recado));
    }

    /**
     * Lista os recados de um paciente. Pode ser chamado:
     *  - pelo próprio paciente autenticado;
     *  - por um médico autenticado que possua PatientShare com esse paciente.
     */
    @Transactional(readOnly = true)
    public List<RecadoMedicoResponse> listarDoPaciente(Long pacienteId,
                                                       Long pacienteSessaoId,
                                                       Long medicoSessaoId) {
        if (pacienteId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paciente é obrigatório.");
        }
        if (pacienteSessaoId == null && medicoSessaoId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Não autenticado.");
        }
        if (pacienteSessaoId != null && !pacienteSessaoId.equals(pacienteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Você não pode acessar recados de outro paciente.");
        }
        if (pacienteSessaoId == null) {
            // Sessão de médico: precisa ter share com esse paciente.
            garantirPermissaoMedicoSobrePaciente(medicoSessaoId, pacienteId);
        }
        if (!profileRepo.existsById(pacienteId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado.");
        }
        return recadoRepo.findByPaciente_IdOrderByDataCriacaoDesc(pacienteId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public RecadoMedicoResponse marcarComoLido(Long recadoId, Long pacienteSessaoId) {
        if (pacienteSessaoId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Apenas o paciente destinatário pode marcar um recado como lido.");
        }
        if (recadoId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recado é obrigatório.");
        }
        RecadoMedico recado = recadoRepo.findById(recadoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recado não encontrado."));

        if (!pacienteSessaoId.equals(recado.getPaciente().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Você não pode marcar como lido um recado de outro paciente.");
        }

        if (recado.getStatus() != StatusRecado.LIDO) {
            recado.setStatus(StatusRecado.LIDO);
            recado.setDataLeitura(Instant.now());
            recado = recadoRepo.save(recado);
        }
        return mapper.toResponse(recado);
    }

    @Transactional(readOnly = true)
    public List<RecadoMedicoResponse> historicoDoMedicoParaPaciente(Long medicoId,
                                                                    Long pacienteId,
                                                                    Long medicoSessaoId) {
        if (medicoSessaoId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Somente médicos autenticados podem consultar este histórico.");
        }
        if (medicoId == null || pacienteId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Médico e paciente são obrigatórios.");
        }
        if (!medicoSessaoId.equals(medicoId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Médico autenticado não pode consultar recados de outro médico.");
        }
        if (!doctorRepo.existsById(medicoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Médico não encontrado.");
        }
        if (!profileRepo.existsById(pacienteId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado.");
        }
        garantirPermissaoMedicoSobrePaciente(medicoId, pacienteId);
        return recadoRepo.findByMedico_IdAndPaciente_IdOrderByDataCriacaoDesc(medicoId, pacienteId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public RecadoMedicoResponse editar(Long recadoId,
                                       RecadoMedicoUpdateRequest body,
                                       Long medicoSessaoId) {
        if (medicoSessaoId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Apenas o médico autor pode editar este recado.");
        }
        if (recadoId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recado é obrigatório.");
        }
        if (body == null
                || body.getTitulo() == null || body.getTitulo().isBlank()
                || body.getMensagem() == null || body.getMensagem().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Título e mensagem são obrigatórios.");
        }
        RecadoMedico recado = recadoRepo.findById(recadoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recado não encontrado."));
        if (!medicoSessaoId.equals(recado.getMedico().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Apenas o médico autor pode editar este recado.");
        }
        recado.setTitulo(body.getTitulo().trim());
        recado.setMensagem(body.getMensagem().trim());
        // Recado alterado volta para "não lido" para que o paciente
        // perceba e leia a nova versão.
        if (recado.getStatus() == StatusRecado.LIDO) {
            recado.setStatus(StatusRecado.NAO_LIDO);
            recado.setDataLeitura(null);
        }
        return mapper.toResponse(recadoRepo.save(recado));
    }

    @Transactional
    public void excluir(Long recadoId, Long medicoSessaoId) {
        if (medicoSessaoId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Apenas o médico autor pode excluir este recado.");
        }
        if (recadoId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recado é obrigatório.");
        }
        RecadoMedico recado = recadoRepo.findById(recadoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recado não encontrado."));
        if (!medicoSessaoId.equals(recado.getMedico().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Apenas o médico autor pode excluir este recado.");
        }
        recadoRepo.delete(recado);
    }

    private void garantirPermissaoMedicoSobrePaciente(Long medicoId, Long pacienteId) {
        PatientShare share = shareRepo.findByPatientIdAndDoctorId(pacienteId, medicoId).orElse(null);
        if (share == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "O médico não possui permissão de acesso ao prontuário deste paciente.");
        }
    }
}