package com.openhealth.service.section;

import com.openhealth.domain.ProntuarioSection;
import com.openhealth.repository.AnamneseRepository;
import org.springframework.stereotype.Component;

@Component
public class AnamneseSectionLoader implements SectionLoader {

    private final AnamneseRepository repo;

    public AnamneseSectionLoader(AnamneseRepository repo) {
        this.repo = repo;
    }

    @Override public ProntuarioSection section() { return ProntuarioSection.ANAMNESE; }

    @Override public Object loadFor(Long patientId) {
        return repo.findByOwnerId(patientId).orElse(null);
    }
}