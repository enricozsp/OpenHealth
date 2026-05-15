package com.openhealth.service.section;

import com.openhealth.domain.ProntuarioSection;
import com.openhealth.mapper.ProfileMapper;
import com.openhealth.repository.ProfileRepository;
import org.springframework.stereotype.Component;

@Component
public class ProfileSectionLoader implements SectionLoader {

    private final ProfileRepository repo;
    private final ProfileMapper mapper;

    public ProfileSectionLoader(ProfileRepository repo, ProfileMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override public ProntuarioSection section() { return ProntuarioSection.PROFILE; }

    @Override public Object loadFor(Long patientId) {
        return repo.findById(patientId).map(mapper::toFull).orElse(null);
    }
}