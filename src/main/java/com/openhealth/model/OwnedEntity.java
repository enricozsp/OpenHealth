package com.openhealth.model;


public interface OwnedEntity {
    Long getId();
    void setId(Long id);
    Long getOwnerId();
    void setOwnerId(Long ownerId);
}