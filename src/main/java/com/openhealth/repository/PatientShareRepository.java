package com.openhealth.repository;

import com.openhealth.model.PatientShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientShareRepository extends JpaRepository<PatientShare, Long> {
    List<PatientShare> findByPatientId(Long patientId);
    List<PatientShare> findByDoctorId(Long doctorId);
    Optional<PatientShare> findByPatientIdAndDoctorId(Long patientId, Long doctorId);
    void deleteByPatientIdAndDoctorId(Long patientId, Long doctorId);
}