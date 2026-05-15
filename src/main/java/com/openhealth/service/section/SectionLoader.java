package com.openhealth.service.section;

import com.openhealth.domain.ProntuarioSection;

public interface SectionLoader {
    ProntuarioSection section();
    Object loadFor(Long patientId);
}