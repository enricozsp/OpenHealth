package com.openhealth.service.section;

import com.openhealth.domain.ProntuarioSection;
import com.openhealth.repository.AllergyRepository;
import org.springframework.stereotype.Component;

@Component
public class AllergiesSectionLoader implements SectionLoader {

    private final AllergyRepository repo;

    public AllergiesSectionLoader(AllergyRepository repo) {
        this.repo = repo;
    }

    @Override public ProntuarioSection section() { return ProntuarioSection.ALLERGIES; }

    @Override public Object loadFor(Long patientId) {
        return repo.findByOwnerId(patientId);
    }
}