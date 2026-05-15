package com.openhealth.service;

import com.openhealth.dto.LoginRequest;
import com.openhealth.dto.RegisterRequest;
import com.openhealth.model.Profile;
import com.openhealth.repository.ProfileRepository;
import com.openhealth.security.PasswordHasher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final ProfileRepository repo;

    public AuthService(ProfileRepository repo) {
        this.repo = repo;
    }

    public boolean emailExists(String email) {
        return repo.findByEmailIgnoreCase(email).isPresent();
    }

    public Profile register(RegisterRequest body) {
        Profile p = new Profile();
        p.setFullName(body.getFullName().trim());
        p.setEmail(body.getEmail().trim().toLowerCase());
        p.setPasswordHash(PasswordHasher.hash(body.getPassword()));
        p.setBirthDate(body.getBirthDate());
        p.setGender(body.getGender());
        p.setDocument(body.getDocument());
        p.setPhone(body.getPhone());
        p.setBloodType(body.getBloodType());
        return repo.save(p);
    }

    public Optional<Profile> authenticate(LoginRequest body) {
        return repo.findByEmailIgnoreCase(body.getEmail().trim())
                .filter(p -> PasswordHasher.verify(body.getPassword(), p.getPasswordHash()));
    }

    public Optional<Profile> findById(Long id) {
        return repo.findById(id);
    }
}