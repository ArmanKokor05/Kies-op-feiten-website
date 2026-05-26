package com.sop.backend.models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_polling_station_name_zipcode",
                        columnNames = {"name", "zipcode"}
                )
        }
)
public class PollingStation extends BaseEntity {

    private String identifier;
    private String name;
    private String zipcode;
    @ManyToOne
    @JoinColumn
    private Municipality municipality;
    @OneToMany(mappedBy = "pollingStation")
    private Set<PollingStationElection> pollingStationElections = new HashSet<>();

    public String getIdentifier() { return identifier; }

    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZipcode() {
        return this.zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public Municipality getMunicipality() {
        return this.municipality;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }

    public Set<PollingStationElection> getPollingStationElections() {
        return this.pollingStationElections;
    }
}
