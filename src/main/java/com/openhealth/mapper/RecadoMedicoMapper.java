package com.openhealth.mapper;

import com.openhealth.dto.RecadoMedicoResponse;
import com.openhealth.model.Doctor;
import com.openhealth.model.Profile;
import com.openhealth.model.RecadoMedico;
import org.springframework.stereotype.Component;

@Component
public class RecadoMedicoMapper {

    public RecadoMedicoResponse toResponse(RecadoMedico r) {
        RecadoMedicoResponse dto = new RecadoMedicoResponse();
        dto.setId(r.getId());
        dto.setTitulo(r.getTitulo());
        dto.setMensagem(r.getMensagem());
        dto.setDataCriacao(r.getDataCriacao());
        dto.setDataLeitura(r.getDataLeitura());
        dto.setStatus(r.getStatus());
        Doctor medico = r.getMedico();
        if (medico != null) {
            dto.setMedicoId(medico.getId());
            dto.setMedicoNome(medico.getFullName());
        }
        Profile paciente = r.getPaciente();
        if (paciente != null) {
            dto.setPacienteId(paciente.getId());
            dto.setPacienteNome(paciente.getFullName());
        }
        return dto;
    }
}