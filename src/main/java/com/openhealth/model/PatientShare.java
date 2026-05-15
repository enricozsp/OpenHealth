package com.openhealth.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "patient_share",
        uniqueConstraints = @UniqueConstraint(columnNames = {"patient_id", "doctor_id"}))
public class PatientShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;

    private Boolean shareProfile;
    private Boolean shareAnamnese;
    private Boolean shareAllergies;
    private Boolean shareVaccines;
    private Boolean shareSurgeries;
    private Boolean shareConsultations;
    private Boolean shareExams;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Boolean getShareProfile() { return shareProfile; }
    public void setShareProfile(Boolean shareProfile) { this.shareProfile = shareProfile; }
    public Boolean getShareAnamnese() { return shareAnamnese; }
    public void setShareAnamnese(Boolean shareAnamnese) { this.shareAnamnese = shareAnamnese; }
    public Boolean getShareAllergies() { return shareAllergies; }
    public void setShareAllergies(Boolean shareAllergies) { this.shareAllergies = shareAllergies; }
    public Boolean getShareVaccines() { return shareVaccines; }
    public void setShareVaccines(Boolean shareVaccines) { this.shareVaccines = shareVaccines; }
    public Boolean getShareSurgeries() { return shareSurgeries; }
    public void setShareSurgeries(Boolean shareSurgeries) { this.shareSurgeries = shareSurgeries; }
    public Boolean getShareConsultations() { return shareConsultations; }
    public void setShareConsultations(Boolean shareConsultations) { this.shareConsultations = shareConsultations; }
    public Boolean getShareExams() { return shareExams; }
    public void setShareExams(Boolean shareExams) { this.shareExams = shareExams; }
}