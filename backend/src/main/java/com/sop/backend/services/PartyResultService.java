package com.sop.backend.services;

import com.sop.backend.dto.PartyResultDTO;
import com.sop.backend.models.PartyResult;
import com.sop.backend.repositories.PartyResultRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartyResultService {

    private final PartyResultRepository partyResultRepository;
    private final PartyColorService partyColorService;  // ✅ NIEUW
    private static final int TOTAL_SEATS = 150;

    private static final Map<String, List<String>> PROVINCIE_TO_KIESKRINGEN = new HashMap<>();
    private static final Map<String, String> KIESKRING_TO_PROVINCIE = new HashMap<>();

    static {
        addMapping("Noord-Holland", Arrays.asList("Amsterdam", "Haarlem", "Alkmaar"));
        addMapping("Zuid-Holland", Arrays.asList("'s-Gravenhage", "s-Gravenhage", "Den Haag", "Rotterdam", "Leiden"));
        addMapping("Utrecht", Arrays.asList("Utrecht"));
        addMapping("Noord-Brabant", Arrays.asList("'s-Hertogenbosch", "s-Hertogenbosch", "Den Bosch", "Tilburg", "Breda"));
        addMapping("Limburg", Arrays.asList("Maastricht"));
        addMapping("Gelderland", Arrays.asList("Arnhem", "Nijmegen"));
        addMapping("Overijssel", Arrays.asList("Zwolle"));
        addMapping("Groningen", Arrays.asList("Groningen"));
        addMapping("Friesland", Arrays.asList("Leeuwarden"));
        addMapping("Drenthe", Arrays.asList("Assen"));
        addMapping("Flevoland", Arrays.asList("Lelystad", "Almere"));
        addMapping("Zeeland", Arrays.asList("Middelburg"));
    }

    private static void addMapping(String provincie, List<String> kieskringen) {
        PROVINCIE_TO_KIESKRINGEN.put(provincie, kieskringen);
        for (String kieskring : kieskringen) {
            KIESKRING_TO_PROVINCIE.put(kieskring.toLowerCase(), provincie);
        }
    }

    public PartyResultService(PartyResultRepository partyResultRepository,
                              PartyColorService partyColorService) {
        this.partyResultRepository = partyResultRepository;
        this.partyColorService = partyColorService;
    }

    public List<PartyResult> getSeatsByYear(int year) {
        List<PartyResult> allResults = partyResultRepository.findByElection_ElectionYear(year);

        List<PartyResult> nationalResults = allResults.stream()
                .filter(pr -> "alle".equalsIgnoreCase(pr.getElection().getRegionId()))
                .collect(Collectors.toList());

        if (nationalResults.isEmpty()) {
            return Collections.emptyList();
        }

        Map<PartyResult, Integer> calculatedSeats = calculateSeatsWithDHondt(nationalResults);
        updateSeats(nationalResults, calculatedSeats);

        return nationalResults.stream()
                .sorted((a, b) -> Integer.compare(
                        b.getSeats() != null ? b.getSeats() : 0,
                        a.getSeats() != null ? a.getSeats() : 0
                ))
                .collect(Collectors.toList());
    }

    public List<PartyResultDTO> getSeatsByYearAsDTO(int year) {
        List<PartyResult> results = getSeatsByYear(year);
        return results.stream()
                .map(pr -> convertToDTO(pr, null, year))
                .collect(Collectors.toList());
    }

    public List<PartyResultDTO> getSeatsByYearAndRegion(int year, String region) {
        List<PartyResult> results;

        if (region == null || region.trim().isEmpty() || "alle".equalsIgnoreCase(region)) {
            results = partyResultRepository.findByElection_ElectionYear(year);
            results = results.stream()
                    .filter(pr -> "alle".equalsIgnoreCase(pr.getElection().getRegionId()))
                    .collect(Collectors.toList());
        }
        else if (isProvincie(region)) {
            List<String> kieskringen = PROVINCIE_TO_KIESKRINGEN.get(region);
            results = new ArrayList<>();

            for (String kieskring : kieskringen) {
                List<PartyResult> kieskringResults = partyResultRepository
                        .findByElection_ElectionYearAndElection_Region(year, kieskring);
                results.addAll(kieskringResults);
            }

            results = aggregateByParty(results);
        }
        else {
            List<PartyResult> allForYear = partyResultRepository.findByElection_ElectionYear(year);

            results = allForYear.stream()
                    .filter(pr -> pr.getElection().getRegion() != null &&
                            pr.getElection().getRegion().toLowerCase().contains(region.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        Map<PartyResult, Integer> calculatedSeats = calculateSeatsWithDHondt(results);
        updateSeats(results, calculatedSeats);

        return results.stream()
                .sorted((a, b) -> Integer.compare(
                        b.getSeats() != null ? b.getSeats() : 0,
                        a.getSeats() != null ? a.getSeats() : 0
                ))
                .map(pr -> convertToDTO(pr, region, year))
                .collect(Collectors.toList());
    }

    public List<PartyResultDTO> getSeatsByYearAndMunicipality(int year, String municipality) {
        List<PartyResult> results;

        if (municipality == null || municipality.trim().isEmpty()) {
            results = partyResultRepository.findByElection_ElectionYear(year);
        } else {
            results = partyResultRepository.findByElection_ElectionYearAndElection_Municipality(year, municipality);
        }

        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        Map<PartyResult, Integer> calculatedSeats = calculateSeatsWithDHondt(results);
        updateSeats(results, calculatedSeats);

        return results.stream()
                .sorted((a, b) -> Integer.compare(
                        b.getSeats() != null ? b.getSeats() : 0,
                        a.getSeats() != null ? a.getSeats() : 0
                ))
                .map(pr -> convertToDTOWithMunicipality(pr, year))
                .collect(Collectors.toList());
    }

    private List<PartyResult> aggregateByParty(List<PartyResult> allResults) {
        Map<String, List<PartyResult>> byParty = allResults.stream()
                .collect(Collectors.groupingBy(PartyResult::getPartyId));

        List<PartyResult> aggregated = new ArrayList<>();

        for (Map.Entry<String, List<PartyResult>> entry : byParty.entrySet()) {
            List<PartyResult> partyResults = entry.getValue();

            PartyResult combined = new PartyResult();
            combined.setPartyId(entry.getKey());
            combined.setPartyName(partyResults.get(0).getPartyName());

            int totalVotes = partyResults.stream()
                    .mapToInt(pr -> pr.getValidVotes() != null ? pr.getValidVotes() : 0)
                    .sum();
            combined.setValidVotes(totalVotes);
            combined.setElection(partyResults.get(0).getElection());

            aggregated.add(combined);
        }

        return aggregated;
    }

    private void updateSeats(List<PartyResult> results, Map<PartyResult, Integer> calculatedSeats) {
        for (PartyResult pr : results) {
            Integer newSeats = calculatedSeats.get(pr);
            if (newSeats != null) {
                pr.setSeats(newSeats);
            }
        }
    }

    private PartyResultDTO convertToDTO(PartyResult pr, String regionOverride, int yearOverride) {
        PartyResultDTO dto = new PartyResultDTO();
        dto.setId(pr.getId());
        dto.setPartyId(pr.getPartyId());
        dto.setPartyName(pr.getPartyName());
        dto.setValidVotes(pr.getValidVotes());
        dto.setSeats(pr.getSeats());

        dto.setColor(partyColorService.getPartyColor(pr.getPartyName()));

        if (regionOverride != null && isProvincie(regionOverride)) {
            dto.setRegion(regionOverride);
            dto.setRegionId(regionOverride);
            dto.setElectionYear(yearOverride);
        } else if (pr.getElection() != null) {
            dto.setRegion(pr.getElection().getRegion());
            dto.setRegionId(pr.getElection().getRegionId());
            dto.setElectionYear(pr.getElection().getElectionYear());
        }

        return dto;
    }

    private PartyResultDTO convertToDTOWithMunicipality(PartyResult pr, int yearOverride) {
        PartyResultDTO dto = new PartyResultDTO();
        dto.setId(pr.getId());
        dto.setPartyId(pr.getPartyId());
        dto.setPartyName(pr.getPartyName());
        dto.setValidVotes(pr.getValidVotes());
        dto.setSeats(pr.getSeats());

        // ✅ UPDATED - Gebruik PartyColorService in plaats van lokale methode
        dto.setColor(partyColorService.getPartyColor(pr.getPartyName()));

        if (pr.getElection() != null) {
            dto.setRegion(pr.getElection().getRegion());
            dto.setRegionId(pr.getElection().getRegionId());
            dto.setElectionYear(pr.getElection().getElectionYear());
        }

        return dto;
    }

    // ❌ VERWIJDERD - getPartyColor() en generateColorFromName()
    // Deze methodes zijn nu in PartyColorService

    private Map<PartyResult, Integer> calculateSeatsWithDHondt(List<PartyResult> parties) {
        Map<PartyResult, Integer> seats = new HashMap<>();
        for (PartyResult pr : parties) {
            seats.put(pr, 0);
        }

        for (int seat = 0; seat < TOTAL_SEATS; seat++) {
            PartyResult winner = null;
            double maxQuotient = 0;

            for (PartyResult pr : parties) {
                long votes = pr.getValidVotes() != null ? pr.getValidVotes() : 0;
                int currentSeats = seats.get(pr);
                double quotient = (double) votes / (currentSeats + 1);

                if (quotient > maxQuotient) {
                    maxQuotient = quotient;
                    winner = pr;
                }
            }

            if (winner != null) {
                seats.put(winner, seats.get(winner) + 1);
            }
        }

        return seats;
    }

    public List<Integer> getAvailableYears() {
        List<PartyResult> allResults = partyResultRepository.findAll();

        return allResults.stream()
                .map(pr -> pr.getElection().getElectionYear())
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public List<String> getAvailableProvinces() {
        List<PartyResult> allResults = partyResultRepository.findAll();

        Set<String> kieskringen = allResults.stream()
                .map(pr -> pr.getElection().getRegion())
                .filter(Objects::nonNull)
                .filter(region -> !region.isEmpty() && !"alle".equalsIgnoreCase(region))
                .collect(Collectors.toSet());

        Set<String> provincies = new HashSet<>();
        for (String kieskring : kieskringen) {
            String provincie = KIESKRING_TO_PROVINCIE.get(kieskring.toLowerCase());
            if (provincie != null) {
                provincies.add(provincie);
            }
        }

        List<String> result = new ArrayList<>(provincies);
        Collections.sort(result);
        return result;
    }

    public List<String> getAvailableKieskringen() {
        List<PartyResult> allResults = partyResultRepository.findAll();

        return allResults.stream()
                .map(pr -> pr.getElection().getRegion())
                .filter(Objects::nonNull)
                .filter(region -> !region.isEmpty() && !"alle".equalsIgnoreCase(region))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getAvailableMunicipalities() {
        List<PartyResult> allResults = partyResultRepository.findAll();

        return allResults.stream()
                .map(pr -> pr.getElection().getMunicipality())
                .filter(Objects::nonNull)
                .filter(municipality -> !municipality.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private boolean isProvincie(String name) {
        return PROVINCIE_TO_KIESKRINGEN.containsKey(name);
    }
}