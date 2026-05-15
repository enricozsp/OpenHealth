package com.openhealth.mapper;

import com.openhealth.model.Doctor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class DoctorMapper {

    public Map<String, Object> toBrief(Doctor d) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", d.getId());
        m.put("fullName", d.getFullName());
        m.put("crm", d.getCrm());
        m.put("email", d.getEmail());
        return m;
    }
}