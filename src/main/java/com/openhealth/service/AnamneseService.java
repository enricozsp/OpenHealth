package com.openhealth.service;

import com.openhealth.model.Anamnese;
import com.openhealth.repository.AnamneseRepository;
import org.springframework.stereotype.Service;

@Service
public class AnamneseService {

    private final AnamneseRepository repo;

    public AnamneseService(AnamneseRepository repo) {
        this.repo = repo;
    }

    public Anamnese loadOrCreate(Long ownerId) {
        return repo.findByOwnerId(ownerId).orElseGet(() -> {
            Anamnese a = new Anamnese();
            a.setOwnerId(ownerId);
            return repo.save(a);
        });
    }

    public Anamnese update(Long ownerId, Anamnese patch) {
        Anamnese current = loadOrCreate(ownerId);
        patch.setId(current.getId());
        patch.setOwnerId(ownerId);
        return repo.save(patch);
    }
}