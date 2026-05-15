package com.openhealth.service;

import com.openhealth.domain.ProntuarioSection;
import com.openhealth.mapper.PatientShareMapper;
import com.openhealth.mapper.ProfileMapper;
import com.openhealth.model.PatientShare;
import com.openhealth.model.Profile;
import com.openhealth.repository.PatientShareRepository;
import com.openhealth.repository.ProfileRepository;
import com.openhealth.service.section.SectionLoader;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ProntuarioService {

    private final PatientShareRepository shareRepo;
    private final ProfileRepository profileRepo;
    private final PatientShareMapper shareMapper;
    private final ProfileMapper profileMapper;
    private final Map<ProntuarioSection, SectionLoader> loaders;

    public ProntuarioService(PatientShareRepository shareRepo,
                             ProfileRepository profileRepo,
                             PatientShareMapper shareMapper,
                             ProfileMapper profileMapper,
                             List<SectionLoader> loaderList) {
        this.shareRepo = shareRepo;
        this.profileRepo = profileRepo;
        this.shareMapper = shareMapper;
        this.profileMapper = profileMapper;
        EnumMap<ProntuarioSection, SectionLoader> map = new EnumMap<>(ProntuarioSection.class);
        for (SectionLoader l : loaderList) map.put(l.section(), l);
        this.loaders = map;
    }

    public List<Map<String, Object>> incomingForDoctor(Long doctorId) {
        List<PatientShare> shares = shareRepo.findByDoctorId(doctorId);
        if (shares.isEmpty()) return List.of();
        Map<Long, Profile> patients = loadPatients(shares);
        List<Map<String, Object>> out = new ArrayList<>(shares.size());
        for (PatientShare s : shares) {
            out.add(shareMapper.toIncomingShareView(s, patients.get(s.getPatientId())));
        }
        out.sort((a, b) -> {
            Object ax = a.get("updatedAt") != null ? a.get("updatedAt") : a.get("createdAt");
            Object bx = b.get("updatedAt") != null ? b.get("updatedAt") : b.get("createdAt");
            if (ax == null && bx == null) return 0;
            if (ax == null) return 1;
            if (bx == null) return -1;
            return bx.toString().compareTo(ax.toString());
        });
        return out;
    }

    public Optional<PatientShare> findShare(Long patientId, Long doctorId) {
        return shareRepo.findByPatientIdAndDoctorId(patientId, doctorId);
    }

    public Map<String, Object> buildProntuario(PatientShare share) {
        Long patientId = share.getPatientId();
        Optional<Profile> opt = profileRepo.findById(patientId);
        Map<String, Object> out = new LinkedHashMap<>();
        if (opt.isEmpty()) {
            out.put("found", false);
            return out;
        }
        Profile p = opt.get();
        out.put("found", true);
        out.put("patient", profileMapper.toBrief(p));
        out.put("sharing", shareMapper.sharingMap(share));
        for (ProntuarioSection sec : ProntuarioSection.values()) {
            if (sec.isSharedIn(share)) {
                SectionLoader loader = loaders.get(sec);
                if (loader != null) {
                    out.put(sec.key(), loader.loadFor(patientId));
                }
            }
        }
        return out;
    }

    private Map<Long, Profile> loadPatients(List<PatientShare> shares) {
        Set<Long> ids = new HashSet<>();
        for (PatientShare s : shares) ids.add(s.getPatientId());
        Map<Long, Profile> map = new HashMap<>();
        profileRepo.findAllById(ids).forEach(p -> map.put(p.getId(), p));
        return map;
    }
}