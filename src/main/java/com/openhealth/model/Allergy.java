package com.openhealth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Entity
@Table(name = "allergy")
public class Allergy implements OwnedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(nullable = false)
    private Long ownerId;

    @NotBlank
    private String substance;

    private String reaction;
    private String severity;
    private LocalDate identifiedOn;

    @Column(length = 2000)
    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getSubstance() { return substance; }
    public void setSubstance(String substance) { this.substance = substance; }
    public String getReaction() { return reaction; }
    public void setReaction(String reaction) { this.reaction = reaction; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public LocalDate getIdentifiedOn() { return identifiedOn; }
    public void setIdentifiedOn(LocalDate identifiedOn) { this.identifiedOn = identifiedOn; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}