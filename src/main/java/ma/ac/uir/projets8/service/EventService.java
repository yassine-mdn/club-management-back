package ma.ac.uir.projets8.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.EventController;
import ma.ac.uir.projets8.exception.*;
import ma.ac.uir.projets8.model.*;
import ma.ac.uir.projets8.model.enums.EventStatus;
import ma.ac.uir.projets8.repository.ClubRepository;
import ma.ac.uir.projets8.repository.EventRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.*;

import static ma.ac.uir.projets8.util.FileGenerator.generateStudentFile;

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

    public Page<Transaction> getTransactionsByEvent(Long id, Integer pageNumber, Integer pageSize) {

        return eventRepository.findAllTransactionsByEventId(id, PageRequest.of(pageNumber,pageSize, Sort.by(Sort.Direction.DESC, "date")));
    }


    public void getEventParticipantsFile(Long id, HttpServletResponse response) {

        try {
            List<Student> members = new ArrayList<>(eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id)).getParticipants());
            String fileName = "rapport-participants" + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + ".xlsx";
            generateStudentFile(response, members, fileName);

        } catch (ClubNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }



    public Page<Student> getParticipantsByEvent(Long id, Integer pageNumber, Integer pageSize) {
       return eventRepository.findAllParticipantsByEventId(id, PageRequest.of(pageNumber,pageSize));
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

    public Page<Event> getEventsPageFiltered(
            String searchKeyword,
            Integer pageNumber,
            Integer pageSize,
            List<EventStatus> statusList
    ) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("date").descending());
        return eventRepository.findAllByFilter(statusList, searchKeyword, pageable);
    }
}
