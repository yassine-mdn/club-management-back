package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import ma.ac.uir.projets8.model.Account;
import ma.ac.uir.projets8.model.ClubDetails;
import ma.ac.uir.projets8.model.Event;
import ma.ac.uir.projets8.model.Transaction;
import ma.ac.uir.projets8.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Create a new event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT')")
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody NewEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(request));
    }

    @Operation(summary = "Get an event by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Optional<Event> optionalEvent = eventService.getEventById(id);
        return optionalEvent.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update an event by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT')")
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody NewEventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id,request));
    }

    @Operation(summary = "Delete an event by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "get the list of transactions", description = "returns the associated list of transactions according to the event with the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT','TREASURER')")
    @GetMapping("{event_id}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionsByBudget(@PathVariable("event_id") Long id) {
        return eventService.getTransactionsByEvent(id);
    }

    @Operation(summary = "get the list of participants", description = "returns the participants list of transactions according to the event with the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT')")
    @GetMapping("{event_id}/participants")
    public ResponseEntity<List<Account>> getParticipantsByEvent(@PathVariable("event_id") Long id) {
        return eventService.getParticipantsByEvent(id);
    }

    @GetMapping()
    public ResponseEntity<List<Event>> getEventsPageable(
            @RequestParam(name = "pageNumber") Integer pageNumber,
            @RequestParam(name = "pageSize") Integer size
    ) {

        return eventService.getEventsPage(pageNumber, size);

    }

    public record NewEventRequest(
            String name,
            String description,
            Date date,
            Integer organizer
    ) {
    }
}
