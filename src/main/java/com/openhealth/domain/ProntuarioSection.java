package com.openhealth.domain;

import com.openhealth.model.PatientShare;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Single source of truth for the sections a patient can share with a doctor.
 *
 * Adding a new section: add a constant here, add the matching boolean column to
 * {@link PatientShare}, and add a {@code SectionLoader} bean. Existing services
 * iterate this enum and don't need to change.
 */
public enum ProntuarioSection {
    PROFILE      ("profile",       PatientShare::getShareProfile,       PatientShare::setShareProfile),
    ANAMNESE     ("anamnese",      PatientShare::getShareAnamnese,      PatientShare::setShareAnamnese),
    ALLERGIES    ("allergies",     PatientShare::getShareAllergies,     PatientShare::setShareAllergies),
    VACCINES     ("vaccines",      PatientShare::getShareVaccines,      PatientShare::setShareVaccines),
    SURGERIES    ("surgeries",     PatientShare::getShareSurgeries,     PatientShare::setShareSurgeries),
    CONSULTATIONS("consultations", PatientShare::getShareConsultations, PatientShare::setShareConsultations),
    EXAMS        ("exams",         PatientShare::getShareExams,         PatientShare::setShareExams);

    private final String key;
    private final Function<PatientShare, Boolean> getter;
    private final BiConsumer<PatientShare, Boolean> setter;

    ProntuarioSection(String key,
                      Function<PatientShare, Boolean> getter,
                      BiConsumer<PatientShare, Boolean> setter) {
        this.key = key;
        this.getter = getter;
        this.setter = setter;
    }

    public String key() { return key; }

    public boolean isSharedIn(PatientShare share) {
        return Boolean.TRUE.equals(getter.apply(share));
    }

    public void apply(PatientShare share, boolean shared) {
        setter.accept(share, shared);
    }
}