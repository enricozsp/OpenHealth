package com.openhealth.model;

import jakarta.persistence.*;

@Entity
@Table(name = "anamnese")
public class Anamnese {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(nullable = false)
    private Long ownerId;

    @Column(length = 4000)
    private String mainComplaint;

    @Column(length = 4000)
    private String currentDiseaseHistory;

    @Column(length = 4000)
    private String personalHistory;

    @Column(length = 4000)
    private String familyHistory;

    @Column(length = 4000)
    private String lifeHabits;

    @Column(length = 4000)
    private String medications;

    @Column(length = 4000)
    private String otherInfo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getMainComplaint() { return mainComplaint; }
    public void setMainComplaint(String mainComplaint) { this.mainComplaint = mainComplaint; }
    public String getCurrentDiseaseHistory() { return currentDiseaseHistory; }
    public void setCurrentDiseaseHistory(String currentDiseaseHistory) { this.currentDiseaseHistory = currentDiseaseHistory; }
    public String getPersonalHistory() { return personalHistory; }
    public void setPersonalHistory(String personalHistory) { this.personalHistory = personalHistory; }
    public String getFamilyHistory() { return familyHistory; }
    public void setFamilyHistory(String familyHistory) { this.familyHistory = familyHistory; }
    public String getLifeHabits() { return lifeHabits; }
    public void setLifeHabits(String lifeHabits) { this.lifeHabits = lifeHabits; }
    public String getMedications() { return medications; }
    public void setMedications(String medications) { this.medications = medications; }
    public String getOtherInfo() { return otherInfo; }
    public void setOtherInfo(String otherInfo) { this.otherInfo = otherInfo; }
}