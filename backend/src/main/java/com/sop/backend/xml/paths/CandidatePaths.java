package com.sop.backend.xml.paths;


import com.sop.backend.xml.XmlElements;

public class CandidatePaths {

    public static final XmlElements[] CANDIDATE_PATH = {
            XmlElements.CANDIDATE,
    };

    public static final XmlElements[] CANDIDATE_NAME_PATH = {
            XmlElements.CANDIDATE_FULL_NAME,
            XmlElements.PERSON_NAME
    };

    public static final XmlElements[] QUALIFYING_ADDRESS = {
            XmlElements.QUALIFYING_ADDRESS,
            XmlElements.LOCALITY,
            XmlElements.LOCALITY_NAME,
    };

    public static final XmlElements[] QUALIFYING_ADDRESS_WITH_COUNTRY = {
            XmlElements.QUALIFYING_ADDRESS,
            XmlElements.COUNTRY,
            XmlElements.LOCALITY,
            XmlElements.LOCALITY_NAME,
    };

    public static final XmlElements[] ELECTION_PATH = {
            XmlElements.CANDIDATE_LIST,
            XmlElements.ELECTION,
            XmlElements.ELECTION_IDENTIFIER,
    };
}
