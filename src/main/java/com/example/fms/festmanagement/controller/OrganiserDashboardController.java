package com.example.fms.festmanagement.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fms.festmanagement.models.Competition;
import com.example.fms.festmanagement.models.Event;
import com.example.fms.festmanagement.models.Organiser;
import com.example.fms.festmanagement.models.Participation;
import com.example.fms.festmanagement.models.SubEvent;
import com.example.fms.festmanagement.models.User;
import com.example.fms.festmanagement.service.AuthenticationService;
import com.example.fms.festmanagement.service.DashboardService;
import com.example.fms.festmanagement.service.MessageService;
import com.example.fms.festmanagement.service.OrganiserDashboardService;
import com.example.fms.festmanagement.service.RegistrationService;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrganiserDashboardController extends Helper{

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private OrganiserDashboardService organiserDashboardService;
    
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private MessageService messageService;

    @GetMapping("organiserdashboard")
    public String OrganiserDashboard(Model model, HttpSession session, RedirectAttributes attributes){
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }

        addDefaultAttributes(model, session);
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        Event e=organiserDashboardService.getEventFromOrganiser(authenticationService.getCurrentUser(session));
        model.addAttribute("event", e);
        return "organiserdashboard";
    }

    @GetMapping("addorganisers")
    public String AddOrganisers(Model model, HttpSession session, RedirectAttributes attributes){
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }
        addDefaultAttributes(model,session);
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        if(!organiserDashboardService.checkHead(authenticationService.getCurrentUser(session))){
            return "redirect:accessdenied";
        }
        Event e=organiserDashboardService.getEventFromOrganiser(authenticationService.getCurrentUser(session));
        model.addAttribute("user", new User());
        model.addAttribute("organiser", new Organiser());
        model.addAttribute("event", e);
        System.out.print(e.toString());
        return "addorganisers";
    }

    @PostMapping("addorganisers")
    public String postAddOrganisers(@ModelAttribute User users, @ModelAttribute Organiser organiser, Model model, HttpSession session, RedirectAttributes attributes) {
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }

        addDefaultAttributes(model, session);
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        if(!organiserDashboardService.checkHead(authenticationService.getCurrentUser(session))){
            return "redirect:accessdenied";
        }
        Event e=organiserDashboardService.getEventFromOrganiser(authenticationService.getCurrentUser(session));
        System.out.println(users.toString());
        System.out.println(organiser.toString());
        organiser.setEventId(e.getEventId());
        registrationService.addOrganisers(users,organiser);
        System.out.println("added");
        messageService.redirectWithSuccess(attributes, "Organiser added to "+e.getEventName());
        return  "redirect:/vieworganisers";
    }

    @GetMapping("vieworganisers")
    public String ViewOrganisers(Model model, HttpSession session){
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }
        addDefaultAttributes(model,session);
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        Event e=organiserDashboardService.getEventFromOrganiser(authenticationService.getCurrentUser(session));
        System.out.println(e.toString());
        model.addAttribute("organisers", organiserDashboardService.getOrganisersByEvent(e));
        model.addAttribute("event", e);
        model.addAttribute("ishead", organiserDashboardService.checkHead(authenticationService.getCurrentUser(session)));
        System.out.println(e.toString());
        return "vieworganisers";
    } 

    @GetMapping("{organiseremail}/delorganiser")
    public String DeleteOrganisers(@PathVariable("organiseremail") String organiserEmail, Model model, HttpSession session, RedirectAttributes attributes){
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }

        addDefaultAttributes(model, session);
        if(!organiserDashboardService.checkHead(authenticationService.getCurrentUser(session))){
            return "redirect:accessdenied";
        }
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        organiserDashboardService.deleteOrganiser(organiserEmail);
        authenticationService.deleteUser(organiserEmail);
        messageService.redirectWithSuccess(attributes, "Organiser deleted");
        return "redirect:/vieworganisers";
    } 

    @GetMapping("viewsubevents")
    public String ViewSubEvents(Model model,HttpSession session){
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }

        addDefaultAttributes(model, session);
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        Event e=organiserDashboardService.getEventFromOrganiser(authenticationService.getCurrentUser(session));
        model.addAttribute("subEvents",organiserDashboardService.getSubEvents(e));
        model.addAttribute("event", e);
        model.addAttribute("ishead", organiserDashboardService.checkHead(authenticationService.getCurrentUser(session)));
        return "viewsubevents";
    }

    @GetMapping("{subEventId}/delsubEvent")
    public String DeleteSubEvent(Model model,HttpSession session,@PathVariable("subEventId") int subEventId,RedirectAttributes attributes){
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }
        
        addDefaultAttributes(model, session);
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        if(!organiserDashboardService.checkHead(authenticationService.getCurrentUser(session))){
            return "redirect:accessdenied";
        }
        Event e=organiserDashboardService.getEventFromOrganiser(authenticationService.getCurrentUser(session));
        organiserDashboardService.DeleteSubEvent(subEventId,e);
        messageService.redirectWithSuccess(attributes, "Sub-Event added to "+e.getEventName());
        return "redirect:/viewsubevents";
    }

    @GetMapping("addsubevent")
    public String AddSubEvents(Model model, HttpSession session, RedirectAttributes attributes){
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }

        addDefaultAttributes(model, session);
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        if(!organiserDashboardService.checkHead(authenticationService.getCurrentUser(session))){
            return "redirect:accessdenied";
        }
        Event e=organiserDashboardService.getEventFromOrganiser(authenticationService.getCurrentUser(session));
        model.addAttribute("subEvent",new SubEvent());
        model.addAttribute("event",e);
        model.addAttribute("startdatestring",new String());
        model.addAttribute("enddatestring",new String());
        return "addsubevent";
    }

    @PostMapping("addsubevent")
    public String PostAddSubEvent(@ModelAttribute SubEvent subEvent, HttpSession session, Model model, RedirectAttributes attributes){
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }

        addDefaultAttributes(model, session);
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        if(!organiserDashboardService.checkHead(authenticationService.getCurrentUser(session))){
            return "redirect:accessdenied";
        }
        Event e=organiserDashboardService.getEventFromOrganiser(authenticationService.getCurrentUser(session));
        subEvent.setEventId(e.getEventId());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(subEvent.toString());
        organiserDashboardService.AddSubEvent(subEvent);
        messageService.redirectWithSuccess(attributes, "Sub-Event added to "+e.getEventName());
        return "redirect:viewsubevents";
    }

    @GetMapping("{subEventId}_viewcompetitions")
    public String ViewCompetition(@PathVariable("subEventId") int subEventId, Model model, HttpSession session){
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }

        addDefaultAttributes(model, session);
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        Event e=organiserDashboardService.getEventFromOrganiser(authenticationService.getCurrentUser(session));
        SubEvent s = organiserDashboardService.getSubEventById(subEventId,e);
        model.addAttribute("competitions",organiserDashboardService.getCompetitions(s));
        model.addAttribute("event",e);
        model.addAttribute("subEvent",s);
        model.addAttribute("ishead", organiserDashboardService.checkHead(authenticationService.getCurrentUser(session)));
        return "viewcompetitions";
    }

    @GetMapping("{subEventId}_addcompetition")
    public String AddCompetition(@PathVariable("subEventId") int subEventId, Model model, HttpSession session){
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }

        addDefaultAttributes(model, session);
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        if(!organiserDashboardService.checkHead(authenticationService.getCurrentUser(session))){
            return "redirect:accessdenied";
        }
        Event e=organiserDashboardService.getEventFromOrganiser(authenticationService.getCurrentUser(session));
        SubEvent s = organiserDashboardService.getSubEventById(subEventId,e);
        model.addAttribute("competition",new Competition());
        model.addAttribute("subEvent",s);
        return "addcompetition";
    }

    @PostMapping("{subEventId}/addcompetition")
    public String PostAddCompetition(@PathVariable("subEventId") int subEventId,@ModelAttribute Competition competition, HttpSession session, Model model,RedirectAttributes attributes){
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }

        addDefaultAttributes(model, session);
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        if(!organiserDashboardService.checkHead(authenticationService.getCurrentUser(session))){
            return "redirect:accessdenied";
        }
        Event e=organiserDashboardService.getEventFromOrganiser(authenticationService.getCurrentUser(session));
        SubEvent s = organiserDashboardService.getSubEventById(subEventId,e);
        competition.setEventId(s.getEventId());
        competition.setSubEventId(s.getSubEventId());
        organiserDashboardService.AddCompetition(competition);
        messageService.redirectWithSuccess(attributes, "Competition added to "+s.getSubEventName()+","+e.getEventName());
        return "redirect:/{subEventId}_viewcompetitions";
    }

    @GetMapping("{subEventId}_{competitionId}_viewparticipants")
    public String ViewParticipants(Model model, @PathVariable("subEventId") int subEventId, @PathVariable("competitionId") int competitionId, HttpSession session){
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }

        addDefaultAttributes(model, session);
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        Event e=organiserDashboardService.getEventFromOrganiser(authenticationService.getCurrentUser(session));
        SubEvent s = organiserDashboardService.getSubEventById(subEventId,e);
        Competition c =organiserDashboardService.getCompetitionById(competitionId,s);
        model.addAttribute("participants",organiserDashboardService.getAllParticipations(c));
        model.addAttribute("event",e);
        model.addAttribute("subEvent",s);
        model.addAttribute("competition",c);
        return "viewparticipants";
    }

    @GetMapping("{subEventId}/{competitionId}/delcompetition")
    public String DeleteCompetition(Model model, @PathVariable("subEventId") int subEventId, @PathVariable("competitionId") int competitionId, HttpSession session){
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }

        addDefaultAttributes(model, session);
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        if(!organiserDashboardService.checkHead(authenticationService.getCurrentUser(session))){
            return "redirect:accessdenied";
        }
        Event e=organiserDashboardService.getEventFromOrganiser(authenticationService.getCurrentUser(session));
        SubEvent s = organiserDashboardService.getSubEventById(subEventId,e);
        Competition c =organiserDashboardService.getCompetitionById(competitionId,s);
        organiserDashboardService.deleteCompetition(c);
        return "redirect:/{subEventId}_viewcompetitions";
    }

    @GetMapping("{subEventId}/{competitionId}/{participantEmail}/delparticipant")
    public String DeleteParticipant(RedirectAttributes attributes,Model model, @PathVariable("subEventId") int subEventId, @PathVariable("competitionId") int competitionId,@PathVariable("participantEmail") String participantEmail, HttpSession session){
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }

        addDefaultAttributes(model, session);
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        Event e=organiserDashboardService.getEventFromOrganiser(authenticationService.getCurrentUser(session));
        SubEvent s = organiserDashboardService.getSubEventById(subEventId,e);
        Competition c =organiserDashboardService.getCompetitionById(competitionId,s);
        organiserDashboardService.deleteParticipation(participantEmail,c);
        messageService.redirectWithSuccess(attributes, "Participation removed");
        return "redirect:/{subEventId}_{competitionId}_viewparticipants";
    }

    @GetMapping("{subEventId}_{competitionId}_updateleaderboard")
    public String UpdateLeaderboard(Model model,@PathVariable("subEventId") int subEventId, @PathVariable("competitionId") int competitionId, String participantEmail, HttpSession session){
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }

        addDefaultAttributes(model, session);
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        Event e=organiserDashboardService.getEventFromOrganiser(authenticationService.getCurrentUser(session));
        SubEvent s = organiserDashboardService.getSubEventById(subEventId,e);
        Competition c =organiserDashboardService.getCompetitionById(competitionId,s);
        model.addAttribute("participants",organiserDashboardService.getAllParticipations(c));
        model.addAttribute("event",e);
        model.addAttribute("subEvent",s);
        model.addAttribute("competition",c);
        return "updateleaderboard";
    }

    @PostMapping("{subEventId}/{competitionId}/{participantEmail}/update")
    public String PostUpdateLeaderboard(Model model, @ModelAttribute Participation allParticipations,@PathVariable("subEventId") int subEventId, @PathVariable("competitionId") int competitionId, @PathVariable("participantEmail") String participantEmail, HttpSession session, RedirectAttributes attributes){
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }

        addDefaultAttributes(model, session);
        if(!model.getAttribute("userRole").equals("organiser")){
            return "redirect:accessdenied";
        }
        Event e=organiserDashboardService.getEventFromOrganiser(authenticationService.getCurrentUser(session));
        SubEvent s = organiserDashboardService.getSubEventById(subEventId,e);
        Competition c =organiserDashboardService.getCompetitionById(competitionId,s);
        allParticipations.setEventId(e.getEventId());
        System.out.println(allParticipations.toString());
        organiserDashboardService.updateParticipant(allParticipations);
        messageService.redirectWithSuccess(attributes, "Updated leaderboard");
        return "redirect:/{subEventId}_{competitionId}_viewparticipants";
    }




    

}
