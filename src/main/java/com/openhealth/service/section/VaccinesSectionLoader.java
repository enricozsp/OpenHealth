package com.openhealth.service.section;

import com.openhealth.domain.ProntuarioSection;
import com.openhealth.repository.VaccineRepository;
import org.springframework.stereotype.Component;

@Component
public class VaccinesSectionLoader implements SectionLoader {

    private final VaccineRepository repo;

    public VaccinesSectionLoader(VaccineRepository repo) {
        this.repo = repo;
    }

    @Override public ProntuarioSection section() { return ProntuarioSection.VACCINES; }

    @Override public Object loadFor(Long patientId) {
        return repo.findByOwnerId(patientId);
    }
}