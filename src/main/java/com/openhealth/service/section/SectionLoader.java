package com.openhealth.service.section;

import com.openhealth.domain.ProntuarioSection;

/**
 * Loads the data for one prontuário section. One bean per {@link ProntuarioSection}.
 * Implementing a new section means adding a new bean — no existing code changes.
 */
public interface SectionLoader {
    ProntuarioSection section();
    Object loadFor(Long patientId);
}