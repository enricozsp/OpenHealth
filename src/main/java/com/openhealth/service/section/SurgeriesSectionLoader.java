package com.openhealth.service.section;

import com.openhealth.domain.ProntuarioSection;
import com.openhealth.repository.SurgeryRepository;
import org.springframework.stereotype.Component;

@Component
public class SurgeriesSectionLoader implements SectionLoader {

    private final SurgeryRepository repo;

    public SurgeriesSectionLoader(SurgeryRepository repo) {
        this.repo = repo;
    }

    @Override public ProntuarioSection section() { return ProntuarioSection.SURGERIES; }

    @Override public Object loadFor(Long patientId) {
        return repo.findByOwnerId(patientId);
    }
}