package com.sop.backend.xml;

public enum XmlElements {
    MANAGING_AUTHORITY("ManagingAuthority"),
    AUTHORITY_IDENTIFIER("AuthorityIdentifier"),
    COUNT("Count"),
    ELECTION("Election"),
    ELECTION_IDENTIFIER("ElectionIdentifier"),
    ELECTION_NAME("ElectionName"),
    ELECTION_CATEGORY("ElectionCategory"),
    ELECTION_DATE("ElectionDate"),
    CONTESTS("Contests"),
    CONTEST("Contest"),
    CONTEST_IDENTIFIER("ContestIdentifier"),
    CONTEST_NAME("ContestName"),
    REPORTING_UNIT_VOTES("ReportingUnitVotes"),
    REPORTING_UNIT_IDENTIFIER("ReportingUnitIdentifier"),
    AFFILIATION_IDENTIFIER("AffiliationIdentifier"),
    CANDIDATE("Candidate"),
    CANDIDATE_IDENTIFIER("CandidateIdentifier"),
    CANDIDATE_FULL_NAME("CandidateFullName"),
    PERSON_NAME("PersonName"),
    INITIALS("NameLine"),
    FIRST_NAME("FirstName"),
    LAST_NAME_PREFIX("LastNamePrefix"),
    LAST_NAME("LastName"),
    QUALIFYING_ADDRESS("QualifyingAddress"),
    LOCALITY("Locality"),
    LOCALITY_NAME("LocalityName"),
    GENDER("Gender"),
    VALID_VOTES("ValidVotes"),
    CANDIDATE_LIST("CandidateList"),
    SELECTION("Selection"),
    AFFILIATION("Affiliation"),
    AFFILIATION_ACRONYM("RegisteredName"),
    COUNTRY("Country");

    private final String value;

    XmlElements(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
