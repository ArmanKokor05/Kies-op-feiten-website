# GitLab Werkafspraken - Team Workflow

## Issue Proces

### Issue Lifecycle
1. **Nieuw Issue** - Issue wordt aangemaakt met duidelijke beschrijving
2. **Triage** - Team beoordeelt en labelt het issue tijdens planning
3. **Planning** - Issue krijgt weight, milestone en eventueel assignee
4. **In Progress** - Iemand pakt issue op en zet status naar "Doing"
5. **Review** - Code review via Merge Request
6. **Done** - Issue wordt gesloten na acceptatie

### Issue Requirements
- Elke issue moet een duidelijke titel en beschrijving hebben
- Acceptatie criteria moeten gedefinieerd zijn
- Labels worden gebruikt voor categorisering (bug, feature, documentation, etc.)

## Milestones, Weights & Assignment

### Milestones
- Milestones representeren sprints of releases
- Alle issues moeten aan een milestone gekoppeld zijn
- Milestone deadlines worden gerespecteerd
- Milestone burndown wordt wekelijks gecontroleerd

### Weights (Story Points)
- Weight scale: 1, 2, 3, 5, 8, 13 
- 1 = Zeer eenvoudig (< 2 uur)
- 2 = Eenvoudig (halve dag)
- 3 = Gemiddeld (1 dag)
- 5 = Complex (2-3 dagen)
- 8 = Zeer complex (1 week)
- 13 = Epic - moet opgesplitst worden

### Assignment
- Issues worden toegewezen tijdens sprintplanning
- Developer kan zelf issues oppakken uit de backlog
- Maximaal 2-3 actieve issues per developer
- Bij blokkades direct communiceren met team

## Retrospective Afspraken

### Belangrijke Aandachtspunten
- **Burndown Chart Review**: Analyseer sprint voortgang 
- **Velocity Tracking**: Bekijk gemiddelde team snelheid en plan realistische sprints
- **Issue Completion**: Controleer welke issues niet af zijn en waarom
- **Process Improvements**: Bespreek wat goed ging en wat beter kan
- **Action Items**: Concrete actiepunten voor volgende sprint
- **Team Health**: Check-in op samenwerking en welzijn

### Retro Format
1. **What went well?** (Continue doing)
2. **What could be improved?** (Stop/Start doing)
3. **Action items** met eigenaar en deadline
4. **Burndown analyse** en velocity review

## Dagelijkse Werkafspraken

### Daily Scrum 
- **Moment**: Elke werkdag
- **Duur**: Maximaal 15 minuten
- **Format**: 
  - Wat heb je gisteren gedaan?
  - Wat ga je vandaag doen?
  - Zijn er blokkades?
- **Updates**: Status van issues updaten in GitLab

### Einde van de Dag
- **Commit je werk**: Alle wijzigingen moeten gecommit en gepusht zijn
- **Issue updates**: Status updaten (tijd logging indien gebruikt)
- **Blokkades communiceren**: Als je morgen niet verder kunt, meld dit
- **Code reviews**: Pending MR's reviewen voor collega's

### Commit Standards
- Gedetailleerde commit messages
- Referentie naar issue nummer 
- Regelmatig committen 
- Feature branches gebruiken voor nieuwe functionaliteit

## Tools 

### Boards
- **To Do**: Nieuwe issues
- **Doing**: Actief in ontwikkeling
- **Review**: Wachtend op review/testing
- **Done**: Afgeronde issues

## Communicatie Afspraken

- Gebruik GitLab comments voor issue-gerelateerde discussie
- Bij urgente zaken: direct contact opnemen
- Wekelijkse sprint review en planning sessies
- Maandelijkse retrospectives

---

*Dit document wordt regelmatig bijgewerkt op basis van team feedback en proces verbeteringen.*

Bronvermelding
Atlassian. (2024). What is a burndown chart? Atlassian Agile Coach. https://www.atlassian.com/agile/tutorials/burndown-charts
Atlassian. (2024). Sprint retrospectives. Atlassian Agile Coach. https://www.atlassian.com/team-playbook/plays/retrospective
Scrum Alliance. (2024). Velocity in agile development. https://www.scrumalliance.org
Scrum.org. (2020). The Scrum Guide: The definitive guide to Scrum. Ken Schwaber & Jeff Sutherland. https://scrumguides.org
Scrum.org. (2024). Sprint retrospective. Professional Scrum Training. https://www.scrum.org/resources/what-is-a-sprint-retrospective