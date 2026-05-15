package com.openhealth.repository;

import com.openhealth.model.Anamnese;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnamneseRepository extends JpaRepository<Anamnese, Long> {
    Optional<Anamnese> findByOwnerId(Long ownerId);
}