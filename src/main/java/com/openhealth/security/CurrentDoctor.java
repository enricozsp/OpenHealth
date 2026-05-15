package com.openhealth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class CurrentDoctor {

    private CurrentDoctor() {}

    public static Long requireId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        Long doctorId = session == null ? null : (Long) session.getAttribute(SessionKeys.DOCTOR_ID);
        if (doctorId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return doctorId;
    }
}