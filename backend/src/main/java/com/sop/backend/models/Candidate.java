package com.sop.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "candidates2")
public class Candidate extends BaseEntity {
    private String firstName;
    private String lastName;
    private String gender;
    private String initials;
    private String qualifyingAddress;
    private int candidateId;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getInitials() { return initials; }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getQualifyingAddress() {
        return qualifyingAddress;
    }

    public void setQualifyingAddress(String qualifyingAddress) {
        this.qualifyingAddress = qualifyingAddress;
    }

    public int getCandidateId() { return candidateId; }

    public void setCandidateId(int candidateId) { this.candidateId = candidateId; }
}

