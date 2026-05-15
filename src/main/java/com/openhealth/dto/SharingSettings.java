package com.openhealth.dto;

public class SharingSettings {
    private Long doctorId;
    private boolean shareProfile;
    private boolean shareAnamnese;
    private boolean shareAllergies;
    private boolean shareVaccines;
    private boolean shareSurgeries;
    private boolean shareConsultations;
    private boolean shareExams;

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public boolean isShareProfile() { return shareProfile; }
    public void setShareProfile(boolean shareProfile) { this.shareProfile = shareProfile; }
    public boolean isShareAnamnese() { return shareAnamnese; }
    public void setShareAnamnese(boolean shareAnamnese) { this.shareAnamnese = shareAnamnese; }
    public boolean isShareAllergies() { return shareAllergies; }
    public void setShareAllergies(boolean shareAllergies) { this.shareAllergies = shareAllergies; }
    public boolean isShareVaccines() { return shareVaccines; }
    public void setShareVaccines(boolean shareVaccines) { this.shareVaccines = shareVaccines; }
    public boolean isShareSurgeries() { return shareSurgeries; }
    public void setShareSurgeries(boolean shareSurgeries) { this.shareSurgeries = shareSurgeries; }
    public boolean isShareConsultations() { return shareConsultations; }
    public void setShareConsultations(boolean shareConsultations) { this.shareConsultations = shareConsultations; }
    public boolean isShareExams() { return shareExams; }
    public void setShareExams(boolean shareExams) { this.shareExams = shareExams; }
}