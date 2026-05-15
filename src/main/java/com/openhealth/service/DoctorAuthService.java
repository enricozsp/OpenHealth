package com.openhealth.service;

import com.openhealth.dto.DoctorRegisterRequest;
import com.openhealth.dto.LoginRequest;
import com.openhealth.model.Doctor;
import com.openhealth.repository.DoctorRepository;
import com.openhealth.security.PasswordHasher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DoctorAuthService {

    private final DoctorRepository repo;

    public DoctorAuthService(DoctorRepository repo) {
        this.repo = repo;
    }

    public boolean emailExists(String email) {
        return repo.findByEmailIgnoreCase(email).isPresent();
    }

    public boolean crmExists(String crm) {
        return repo.findByCrmIgnoreCase(crm).isPresent();
    }

    public Doctor register(DoctorRegisterRequest body) {
        Doctor d = new Doctor();
        d.setFullName(body.getFullName().trim());
        d.setEmail(body.getEmail().trim().toLowerCase());
        d.setCrm(body.getCrm().trim());
        d.setPasswordHash(PasswordHasher.hash(body.getPassword()));
        return repo.save(d);
    }

    public Optional<Doctor> authenticate(LoginRequest body) {
        return repo.findByEmailIgnoreCase(body.getEmail().trim())
                .filter(d -> PasswordHasher.verify(body.getPassword(), d.getPasswordHash()));
    }

    public Optional<Doctor> findById(Long id) {
        return repo.findById(id);
    }
}