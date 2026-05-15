package com.openhealth.repository;

import com.openhealth.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmailIgnoreCase(String email);
    Optional<Doctor> findByCrmIgnoreCase(String crm);
}