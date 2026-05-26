package com.sop.backend.services;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PartyColorService {

    private static final Map<String, String> PARTY_COLORS = Map.ofEntries(
            Map.entry("VVD", "#FF8C00"),
            Map.entry("PVV", "#1C4688"),
            Map.entry("D66", "#32CD32"),
            Map.entry("BBB", "#00AEEF"),
            Map.entry("CDA", "#00A758"),
            Map.entry("GroenLinks", "#6CB52D"),
            Map.entry("PvdA", "#502379"),
            Map.entry("SP", "#FF0000"),
            Map.entry("ChristenUnie", "#00AEEF"),
            Map.entry("PvdD", "#006C2E"),
            Map.entry("SGP", "#F37021"),
            Map.entry("DENK", "#00C9B7"),
            Map.entry("FvD", "#8B0000"),
            Map.entry("Volt", "#502379"),
            Map.entry("JA21", "#4169E1"),
            Map.entry("50PLUS", "#800080"),
            Map.entry("BIJ1", "#FFFF00"),
            Map.entry("NSC", "#00CED1"),
            Map.entry("BVNL", "#8B0000")
    );


    public String getPartyColor(String partyName) {
        if (partyName == null || partyName.isEmpty()) {
            return "#CCCCCC";
        }

        if (PARTY_COLORS.containsKey(partyName)) {
            return PARTY_COLORS.get(partyName);
        }

        String normalized = normalizePartyName(partyName);
        for (Map.Entry<String, String> entry : PARTY_COLORS.entrySet()) {
            String knownParty = normalizePartyName(entry.getKey());
            if (normalized.contains(knownParty) || knownParty.contains(normalized)) {
                return entry.getValue();
            }
        }

        return generateColorFromName(partyName);
    }


    private String normalizePartyName(String name) {
        return name.toLowerCase()
                .replaceAll("[\\s\\-_()]", "")
                .replaceAll("partijvoor", "")
                .replaceAll("partijvan", "");
    }


    private String generateColorFromName(String name) {
        int hash = name.hashCode();

        int r = 60 + (Math.abs(hash) % 140);
        int g = 60 + (Math.abs(hash >> 8) % 140);
        int b = 60 + (Math.abs(hash >> 16) % 140);

        return String.format("#%02X%02X%02X", r, g, b);
    }


    public boolean hasKnownColor(String partyName) {
        if (partyName == null || partyName.isEmpty()) {
            return false;
        }

        if (PARTY_COLORS.containsKey(partyName)) {
            return true;
        }

        String normalized = normalizePartyName(partyName);
        for (String knownParty : PARTY_COLORS.keySet()) {
            String knownNormalized = normalizePartyName(knownParty);
            if (normalized.contains(knownNormalized) || knownNormalized.contains(normalized)) {
                return true;
            }
        }

        return false;
    }


    public Map<String, String> getAllKnownColors() {
        return Map.copyOf(PARTY_COLORS);
    }
}