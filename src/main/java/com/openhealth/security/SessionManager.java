package com.openhealth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;


@Component
public class SessionManager {

    public void loginAsPatient(HttpServletRequest req, Long patientId) {
        HttpSession session = req.getSession(true);
        session.removeAttribute(SessionKeys.DOCTOR_ID);
        session.setAttribute(SessionKeys.USER_ID, patientId);
    }

    public void loginAsDoctor(HttpServletRequest req, Long doctorId) {
        HttpSession session = req.getSession(true);
        session.removeAttribute(SessionKeys.USER_ID);
        session.setAttribute(SessionKeys.DOCTOR_ID, doctorId);
    }

    public void logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
    }

    public boolean isPatientAuthenticated(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute(SessionKeys.USER_ID) != null;
    }

    public boolean isDoctorAuthenticated(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute(SessionKeys.DOCTOR_ID) != null;
    }
}