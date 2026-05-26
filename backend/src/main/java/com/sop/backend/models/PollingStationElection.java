package com.sop.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PollingStationElection extends BaseEntity {

    @ManyToOne
    @JoinColumn
    private PollingStation pollingStation;

    @ManyToOne
    @JoinColumn
    private Election2 election;

    public PollingStation getPollingStation() { return pollingStation; }

    public void setPollingStation(PollingStation pollingStation) { this.pollingStation = pollingStation; }

    public Election2 getElection() { return election; }

    public void setElection(Election2 election) { this.election = election; }
}
