package com.openhealth.service.section;

import com.openhealth.domain.ProntuarioSection;
import com.openhealth.repository.ClinicalExamRepository;
import org.springframework.stereotype.Component;

@Component
public class ExamsSectionLoader implements SectionLoader {

    private final ClinicalExamRepository repo;

    public ExamsSectionLoader(ClinicalExamRepository repo) {
        this.repo = repo;
    }

    @Override public ProntuarioSection section() { return ProntuarioSection.EXAMS; }

    @Override public Object loadFor(Long patientId) {
        return repo.findByOwnerId(patientId);
    }
}