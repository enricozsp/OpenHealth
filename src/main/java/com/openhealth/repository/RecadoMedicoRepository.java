package com.openhealth.repository;

import com.openhealth.model.RecadoMedico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecadoMedicoRepository extends JpaRepository<RecadoMedico, Long> {

    List<RecadoMedico> findByPaciente_IdOrderByDataCriacaoDesc(Long pacienteId);

    List<RecadoMedico> findByMedico_IdAndPaciente_IdOrderByDataCriacaoDesc(Long medicoId, Long pacienteId);
}