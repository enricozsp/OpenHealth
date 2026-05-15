package com.openhealth.mapper;

import com.openhealth.model.Profile;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ProfileMapper {

    public Map<String, Object> toBrief(Profile p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("fullName", p.getFullName());
        m.put("email", p.getEmail());
        return m;
    }

    public Map<String, Object> toFull(Profile p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("fullName", p.getFullName());
        m.put("email", p.getEmail());
        m.put("birthDate", p.getBirthDate());
        m.put("gender", p.getGender());
        m.put("bloodType", p.getBloodType());
        m.put("document", p.getDocument());
        m.put("phone", p.getPhone());
        m.put("emergencyContact", p.getEmergencyContact());
        m.put("notes", p.getNotes());
        return m;
    }
}