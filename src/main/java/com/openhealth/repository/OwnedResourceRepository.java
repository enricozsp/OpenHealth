package com.openhealth.repository;

import com.openhealth.model.OwnedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface OwnedResourceRepository<T extends OwnedEntity> extends JpaRepository<T, Long> {
    List<T> findByOwnerId(Long ownerId);
    Optional<T> findByIdAndOwnerId(Long id, Long ownerId);
}