package com.sop.backend;

import com.sop.backend.models.Party;
import com.sop.backend.models.Question;
import com.sop.backend.repositories.PartyRepository;
import com.sop.backend.repositories.PartyResultRepository;
import com.sop.backend.repositories.QuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PartyRepository partyRepository;
    private final PartyResultRepository partyResultRepository;
    private final QuestionRepository questionRepository;

    public DataSeeder(PartyRepository partyRepository,
                      PartyResultRepository partyResultRepository,
                      QuestionRepository questionRepository) {
        this.partyRepository = partyRepository;
        this.partyResultRepository = partyResultRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public void run(String... args) {
        seedAllParties();
        seedQuestions();
    }

    private void seedAllParties() {
        if (partyRepository.count() > 0) {
            System.out.println("Parties already exist, skipping");
            return;
        }

        List<String> allPartyNames = partyResultRepository.findAll().stream()
                .map(pr -> pr.getPartyName())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        for (String partyName : allPartyNames) {
            Party party = new Party();
            party.setName(partyName);
            party.setColor("#CCCCCC");
            partyRepository.save(party);
        }

        System.out.println(allPartyNames.size() + " parties loaded");
    }

    private void seedQuestions() {
        System.out.println("Seeding questions...");
        createAllQuestions();
        System.out.println("Questions seeded/updated: " + questionRepository.count());
    }

    private void createAllQuestions() {
        createQuestion(
                "BTW verhogen",
                "De BTW moet omhoog naar 23%",
                "https://www.tweedekamer.nl/kamerstukken/moties/detail?id=2024Z11821&did=2024D28376",
                "Tweede Kamer - Motie 36471-85"
        );

        createQuestion(
                "Minimumloon Verhogen",
                "Het minimum loon moet omhoog in 2026",
                "https://www.tweedekamer.nl/kamerstukken/wetsvoorstellen/detail?cfg=wetsvoorsteldetails&qry=wetsvoorstel:36488",
                "Tweede Kamer - Wetsvoorstel 36488"
        );

        createQuestion(
                "Kernenergie",
                "Er moet een operationele kerncentrale zijn in 2035",
                "https://www.tweedekamer.nl/kamerstukken/moties/detail?id=2025Z05311&did=2025D12156",
                "Tweede Kamer - Motie 32645-146"
        );

        createQuestion(
                "Lagere pensioenleeftijd",
                "De pensioenleeftijd moet omlaag",
                "https://www.tweedekamer.nl/kamerstukken/wetsvoorstellen/detail?cfg=wetsvoorsteldetails&qry=wetsvoorstel:35223",
                "Tweede Kamer - Wetsvoorstel 35223"
        );

        createQuestion(
                "Defensie-uitgaven Verhogen",
                "Nederland moet Defensie-uitgaven Verhogen",
                "https://www.tweedekamer.nl/kamerstukken/moties/detail?id=2025Z08210&did=2025D18744",
                "Tweede Kamer - Motie 22112-4037"
        );

        createQuestion(
                "Asiel en Immigratie Beperken",
                "Nederland moet Asiel en Immigratie Beperken",
                "https://www.tweedekamer.nl/kamerstukken/moties/detail?id=2024Z17782&did=2024D42690",
                "Tweede Kamer - Begroting 36600-VI"
        );

        createQuestion(
                "Basisbeurs Terugbrengen",
                "We moeten de Basisbeurs Terugbrengen",
                "https://www.tweedekamer.nl/kamerstukken/wetsvoorstellen/detail?cfg=wetsvoorsteldetails&qry=wetsvoorstel:36229",
                "Tweede Kamer - Wetsvoorstel 36229"
        );

        createQuestion(
                "Woningbouw boven windmolens",
                "Bij concurrentie om grond moet woningbouw prioriteit krijgen boven windmolens",
                "https://www.tweedekamer.nl/kamerstukken/wetsvoorstellen/detail?cfg=wetsvoorsteldetails&qry=wetsvoorstel:36600-XXIII",
                "Tweede Kamer - Begroting 36600-XXIII"
        );

        createQuestion(
                "EU versterken",
                "Nederland moet inzetten op een sterkere Europese Unie",
                "https://www.tweedekamer.nl/kamerstukken/moties/detail?id=2024Z08385&did=2024D19603",
                "Tweede Kamer - Motie 36476-6"
        );

        createQuestion(
                "Gezondheidszorg Wachttijden",
                "Nederland moet zorgen dat de zorg wachtijden achteruit gaan",
                "https://www.tweedekamer.nl/kamerstukken/moties/detail?id=2024Z20768&did=2024D49017",
                "Tweede Kamer - Motie 25424-717"
        );

        createQuestion(
                "Sociale Woningbouw",
                "Nederland moet zorgen voor betere Sociale woningbouw",
                "https://www.tweedekamer.nl/kamerstukken/wetsvoorstellen/detail?cfg=wetsvoorsteldetails&qry=wetsvoorstel:36600-XXII",
                "Tweede Kamer - Begroting 36600-XXII"
        );

        createQuestion(
                "Landbouw en Stikstof",
                "Nederland moet het stikstofbeleid aanpassen om de landbouw te beschermen",
                "https://www.tweedekamer.nl/kamerstukken/moties/detail?id=2025Z10311&did=2025D23547",
                "Tweede Kamer - Stemmingen 35334"
        );

        createQuestion(
                "CO2-heffing Afschaffen",
                "CO2-heffing op de industrie moet zo snel mogelijk worden afgeschaft",
                "https://www.tweedekamer.nl/kamerstukken/moties/detail?id=2025Z12633&did=2025D28751",
                "Tweede Kamer - Motie 36725-11"
        );

        createQuestion(
                "Gratis OV voor studenten",
                "OV voor studenten moet weer gratis worden",
                "https://www.tweedekamer.nl/kamerstukken/moties/detail?id=2025Z20050&did=2025D47153",
                "Tweede Kamer - Motie 23645-873"
        );

        createQuestion(
                "Box 3-belasting hervormen",
                "Belasting in Box 3 moet gebaseerd zijn op werkelijk rendement, niet op forfaitair rendement",
                "https://www.tweedekamer.nl/kamerstukken/wetsvoorstellen/detail?id=2025Z04801&dossier=36706",
                "Tweede Kamer - Wetsvoorstel 36706"
        );
    }

    private void createQuestion(String title, String question, String sourceUrl, String sourceName) {
        Question existing = questionRepository.findByTitle(title);

        if (existing != null) {
            existing.setQuestion(question);
            existing.setSourceUrl(sourceUrl);
            existing.setSourceName(sourceName);
            questionRepository.save(existing);
            return;
        }

        Question q = new Question();
        q.setTitle(title);
        q.setQuestion(question);
        q.setSourceUrl(sourceUrl);
        q.setSourceName(sourceName);
        questionRepository.save(q);
    }
}
