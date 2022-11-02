package com.example.fms.festmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fms.festmanagement.doa.CompetitionRepo;
import com.example.fms.festmanagement.doa.EventRepo;
import com.example.fms.festmanagement.doa.OrganiserRepo;
import com.example.fms.festmanagement.doa.ParticipationRepo;
import com.example.fms.festmanagement.doa.QueriesRepo;
import com.example.fms.festmanagement.doa.SubEventRepo;
import com.example.fms.festmanagement.models.Competition;
import com.example.fms.festmanagement.models.Event;
import com.example.fms.festmanagement.models.Organiser;
import com.example.fms.festmanagement.models.Participant;
import com.example.fms.festmanagement.models.SubEvent;

@Service
public class OrganiserDashboardService {

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private OrganiserRepo organiserRepo;

    @Autowired
    private SubEventRepo subEventRepo;

    @Autowired
    private CompetitionRepo competitionRepo;

    @Autowired
    private ParticipationRepo participationRepo;

    @Autowired
    private QueriesRepo queriesRepo;

    public Event getEventFromOrganiser(String email){
        return queriesRepo.selectEventFromOrganiser(email);
    }

    public List<Organiser> getOrganisersByEvent(Event e) {
        return queriesRepo.getOrganiserByEvent(e.getEventId());
    }

    public List<SubEvent> getSubEvents(Event e){
        return queriesRepo.getSubEventsByEvent(e.getEventId());
    }

    public void AddSubEvent(SubEvent subEvent) {
        subEventRepo.insertSubEvent(subEvent);
    }

    public List<Competition> getCompetions(SubEvent subEvent) {
        return queriesRepo.getCompetitions(subEvent.getSubEventId(),subEvent.getEventId());
    }

    public SubEvent getSubEventById(int i, Event e){
        return queriesRepo.getSubEventById(i,e.getEventId());
    }

    public void AddCompetition(Competition competition) {
        competitionRepo.insertCompetition(competition);
    }

    public Competition getCompetitionById(int competitionId, SubEvent s) {
        return queriesRepo.getCompetitionById(competitionId,s.getSubEventId(),s.getEventId());
    }

    public List<Participant> getAllParticipants(Competition c) {
        return queriesRepo.getAllParticipants(c.getCompetitionId(),c.getSubEventId(),c.getEventId());
    }

    public void deleteParticipation(String participantEmail, Competition c) {
        participationRepo.deleteParticipation(participantEmail,c.getCompetitionId(),c.getSubEventId(),c.getEventId());
    }
}
