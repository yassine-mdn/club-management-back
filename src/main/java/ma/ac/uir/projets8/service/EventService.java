package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.EventController;
import ma.ac.uir.projets8.exception.*;
import ma.ac.uir.projets8.model.Account;
import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.model.Event;
import ma.ac.uir.projets8.model.Transaction;
import ma.ac.uir.projets8.model.enums.EventStatus;
import ma.ac.uir.projets8.repository.ClubRepository;
import ma.ac.uir.projets8.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final ClubRepository clubRepository;

    @CacheEvict(value = "events", allEntries = true)
    public Event createEvent(EventController.NewEventRequest request) {
        Event event = new Event();
        event.setName(request.name());
        event.setDate(request.date());
        event.setDescription(request.description());
        event.setStatus(EventStatus.REQUESTED);
        event.setOrganisateur(clubRepository.findById(request.organizer()).orElseThrow(() -> new ClubNotFoundException(request.organizer())));
        return eventRepository.save(event);
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @CacheEvict(value = "events", allEntries = true)
    public Event updateEvent(Long id, EventController.NewEventRequest request) {
        return eventRepository.findById(id).map(event -> {
            if (!request.name().isEmpty())
                event.setName(request.name());
            if (request.date() != null)
                event.setDate(request.date());
            if (!request.description().isEmpty())
                event.setDescription(request.description());
            event.setOrganisateur(clubRepository.findById(request.organizer()).orElseThrow(() -> new ClubNotFoundException(request.organizer())));
            return eventRepository.save(event);
        }).orElseThrow(() -> new EventNotFoundException(id));
    }

    @CacheEvict(value = "events", allEntries = true)
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException(id);
        }
        eventRepository.deleteById(id);
    }

    public ResponseEntity<List<Transaction>> getTransactionsByEvent(Long id) {
        try {
            return ResponseEntity.ok(new ArrayList<>(eventRepository.findById(id).orElseThrow(() -> new BudgetNotFoundException(id)).getTranscations()));
        } catch (MeetingNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    //TODO : make it return an exel file with all the participants instead of a list
    public ResponseEntity<List<Account>> getParticipantsByEvent(Long id) {
        try {
            return ResponseEntity.ok(new ArrayList<>(eventRepository.findById(id).orElseThrow(() -> new BudgetNotFoundException(id)).getParticipants()));
        } catch (MeetingNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Cacheable(value = "events")
    public ResponseEntity<List<Event>> getEventsPage(Integer pageNumber, Integer size) {

        if (pageNumber < 0 || size < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Page<Event> resultPage = eventRepository.findAll(PageRequest.of(pageNumber, size));
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }

}
