package com.openhealth.mapper;

import com.openhealth.domain.ProntuarioSection;
import com.openhealth.model.Doctor;
import com.openhealth.model.PatientShare;
import com.openhealth.model.Profile;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class PatientShareMapper {

    public Map<String, Boolean> sharingMap(PatientShare share) {
        Map<String, Boolean> m = new LinkedHashMap<>();
        for (ProntuarioSection s : ProntuarioSection.values()) {
            m.put(s.key(), s.isSharedIn(share));
        }
        return m;
    }

    public int countSharedSections(PatientShare share) {
        int n = 0;
        for (ProntuarioSection s : ProntuarioSection.values()) {
            if (s.isSharedIn(share)) n++;
        }
        return n;
    }


    public Map<String, Object> toSharedDoctorView(PatientShare share, Doctor doctor) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("doctorId", share.getDoctorId());
        row.put("doctorName", doctor != null ? doctor.getFullName() : "(médico removido)");
        row.put("doctorCrm", doctor != null ? doctor.getCrm() : null);
        row.put("doctorEmail", doctor != null ? doctor.getEmail() : null);
        row.put("createdAt", share.getCreatedAt());
        row.put("updatedAt", share.getUpdatedAt());
        for (ProntuarioSection s : ProntuarioSection.values()) {
            row.put(camelKey(s), s.isSharedIn(share));
        }
        return row;
    }

    /** Doctor-facing view: "who shared with me". */
    public Map<String, Object> toIncomingShareView(PatientShare share, Profile patient) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("patientId", share.getPatientId());
        row.put("patientName", patient != null ? patient.getFullName() : "(paciente removido)");
        row.put("patientEmail", patient != null ? patient.getEmail() : null);
        row.put("createdAt", share.getCreatedAt());
        row.put("updatedAt", share.getUpdatedAt());
        row.put("sharing", sharingMap(share));
        row.put("sectionsCount", countSharedSections(share));
        return row;
    }

    /** Maps the ProntuarioSection key -> the JSON field used in patient-facing rows ("shareProfile"). */
    private static String camelKey(ProntuarioSection s) {
        String k = s.key();
        return "share" + Character.toUpperCase(k.charAt(0)) + k.substring(1);
    }
}