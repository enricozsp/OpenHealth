package com.openhealth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Entity
@Table(name = "surgery")
public class Surgery implements OwnedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(nullable = false)
    private Long ownerId;

    @NotBlank
    private String procedure;

    private LocalDate performedOn;
    private String hospital;
    private String surgeon;
    private String anesthesia;

    @Column(length = 4000)
    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getProcedure() { return procedure; }
    public void setProcedure(String procedure) { this.procedure = procedure; }
    public LocalDate getPerformedOn() { return performedOn; }
    public void setPerformedOn(LocalDate performedOn) { this.performedOn = performedOn; }
    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    public String getSurgeon() { return surgeon; }
    public void setSurgeon(String surgeon) { this.surgeon = surgeon; }
    public String getAnesthesia() { return anesthesia; }
    public void setAnesthesia(String anesthesia) { this.anesthesia = anesthesia; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}