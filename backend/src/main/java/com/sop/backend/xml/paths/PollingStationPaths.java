package com.sop.backend.xml.paths;

import com.sop.backend.xml.XmlElements;

public final class PollingStationPaths {

    public static final XmlElements[] POLLING_STATION_NAME_PATH = {
            XmlElements.COUNT,
            XmlElements.ELECTION,
            XmlElements.CONTESTS,
            XmlElements.CONTEST,
            XmlElements.REPORTING_UNIT_VOTES
    };
}
