package com.openhealth.service;

import com.openhealth.model.OwnedEntity;
import com.openhealth.repository.OwnedResourceRepository;

import java.util.List;
import java.util.Optional;


public class OwnedResourceService<T extends OwnedEntity> {

    private final OwnedResourceRepository<T> repo;

    public OwnedResourceService(OwnedResourceRepository<T> repo) {
        this.repo = repo;
    }

    public List<T> listFor(Long ownerId) {
        return repo.findByOwnerId(ownerId);
    }

    public T create(T body, Long ownerId) {
        body.setId(null);
        body.setOwnerId(ownerId);
        return repo.save(body);
    }

    public Optional<T> update(Long id, T body, Long ownerId) {
        return repo.findByIdAndOwnerId(id, ownerId).map(existing -> {
            body.setId(id);
            body.setOwnerId(ownerId);
            return repo.save(body);
        });
    }

    public boolean delete(Long id, Long ownerId) {
        return repo.findByIdAndOwnerId(id, ownerId).map(existing -> {
            repo.delete(existing);
            return true;
        }).orElse(false);
    }
}