package com.openhealth.service;

import com.openhealth.domain.ProntuarioSection;
import com.openhealth.dto.SharingSettings;
import com.openhealth.mapper.DoctorMapper;
import com.openhealth.mapper.PatientShareMapper;
import com.openhealth.model.Doctor;
import com.openhealth.model.PatientShare;
import com.openhealth.repository.DoctorRepository;
import com.openhealth.repository.PatientShareRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class SharingService {

    private final PatientShareRepository shareRepo;
    private final DoctorRepository doctorRepo;
    private final PatientShareMapper shareMapper;
    private final DoctorMapper doctorMapper;

    public SharingService(PatientShareRepository shareRepo,
                          DoctorRepository doctorRepo,
                          PatientShareMapper shareMapper,
                          DoctorMapper doctorMapper) {
        this.shareRepo = shareRepo;
        this.doctorRepo = doctorRepo;
        this.shareMapper = shareMapper;
        this.doctorMapper = doctorMapper;
    }

    public List<Map<String, Object>> sharesOfPatient(Long patientId) {
        List<PatientShare> shares = shareRepo.findByPatientId(patientId);
        if (shares.isEmpty()) return List.of();
        Map<Long, Doctor> doctors = loadDoctors(shares);
        List<Map<String, Object>> out = new ArrayList<>(shares.size());
        for (PatientShare s : shares) {
            out.add(shareMapper.toSharedDoctorView(s, doctors.get(s.getDoctorId())));
        }
        out.sort((a, b) -> ((String) a.get("doctorName")).compareToIgnoreCase((String) b.get("doctorName")));
        return out;
    }

    public List<Map<String, Object>> listAllDoctors() {
        List<Doctor> all = doctorRepo.findAll();
        all.sort((a, b) -> a.getFullName().compareToIgnoreCase(b.getFullName()));
        List<Map<String, Object>> out = new ArrayList<>(all.size());
        for (Doctor d : all) out.add(doctorMapper.toBrief(d));
        return out;
    }

    public Optional<Doctor> findDoctor(Long doctorId) {
        return doctorRepo.findById(doctorId);
    }

    public PatientShare upsert(Long patientId, SharingSettings body) {
        PatientShare share = shareRepo.findByPatientIdAndDoctorId(patientId, body.getDoctorId())
                .orElseGet(() -> {
                    PatientShare s = new PatientShare();
                    s.setPatientId(patientId);
                    s.setDoctorId(body.getDoctorId());
                    s.setCreatedAt(Instant.now());
                    return s;
                });
        applyFlags(share, body);
        share.setUpdatedAt(Instant.now());
        return shareRepo.save(share);
    }

    @Transactional
    public void revoke(Long patientId, Long doctorId) {
        shareRepo.deleteByPatientIdAndDoctorId(patientId, doctorId);
    }

    private void applyFlags(PatientShare share, SharingSettings body) {
        for (ProntuarioSection sec : ProntuarioSection.values()) {
            sec.apply(share, isShared(body, sec));
        }
    }

    private static boolean isShared(SharingSettings body, ProntuarioSection sec) {
        return switch (sec) {
            case PROFILE       -> body.isShareProfile();
            case ANAMNESE      -> body.isShareAnamnese();
            case ALLERGIES     -> body.isShareAllergies();
            case VACCINES      -> body.isShareVaccines();
            case SURGERIES     -> body.isShareSurgeries();
            case CONSULTATIONS -> body.isShareConsultations();
            case EXAMS         -> body.isShareExams();
        };
    }

    private Map<Long, Doctor> loadDoctors(List<PatientShare> shares) {
        Set<Long> ids = new HashSet<>();
        for (PatientShare s : shares) ids.add(s.getDoctorId());
        Map<Long, Doctor> map = new HashMap<>();
        doctorRepo.findAllById(ids).forEach(d -> map.put(d.getId(), d));
        return map;
    }
}