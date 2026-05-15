package com.openhealth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (!path.startsWith("/api/")
                || path.startsWith("/api/auth/")
                || path.startsWith("/api/doctor-auth/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);

        if (path.startsWith("/api/doctor/")) {
            Object doctorId = session == null ? null : session.getAttribute(SessionKeys.DOCTOR_ID);
            if (doctorId == null) {
                unauthorized(response);
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        Object userId = session == null ? null : session.getAttribute(SessionKeys.USER_ID);
        if (userId == null) {
            unauthorized(response);
            return;
        }
        chain.doFilter(request, response);
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"Não autenticado\"}");
    }
}