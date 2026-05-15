package com.openhealth.service;

import com.openhealth.model.Profile;
import com.openhealth.repository.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfileService {

    private final ProfileRepository repo;

    public ProfileService(ProfileRepository repo) {
        this.repo = repo;
    }

    public Profile loadOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    public Profile update(Long id, Profile patch) {
        Profile current = loadOrThrow(id);
        if (patch.getFullName() != null) current.setFullName(patch.getFullName());
        current.setBirthDate(patch.getBirthDate());
        current.setGender(patch.getGender());
        current.setBloodType(patch.getBloodType());
        current.setDocument(patch.getDocument());
        current.setPhone(patch.getPhone());
        current.setEmergencyContact(patch.getEmergencyContact());
        current.setNotes(patch.getNotes());
        if (patch.getEmail() != null && !patch.getEmail().isBlank()) {
            current.setEmail(patch.getEmail().trim().toLowerCase());
        }
        return repo.save(current);
    }
}