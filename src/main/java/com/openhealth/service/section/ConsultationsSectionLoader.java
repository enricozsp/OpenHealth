package com.openhealth.service.section;

import com.openhealth.domain.ProntuarioSection;
import com.openhealth.repository.ConsultationRepository;
import org.springframework.stereotype.Component;

@Component
public class ConsultationsSectionLoader implements SectionLoader {

    private final ConsultationRepository repo;

    public ConsultationsSectionLoader(ConsultationRepository repo) {
        this.repo = repo;
    }

    @Override public ProntuarioSection section() { return ProntuarioSection.CONSULTATIONS; }

    @Override public Object loadFor(Long patientId) {
        return repo.findByOwnerId(patientId);
    }
}