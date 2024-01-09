package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import ma.ac.uir.projets8.exception.PageOutOfBoundsException;
import ma.ac.uir.projets8.model.*;
import ma.ac.uir.projets8.model.enums.EventStatus;
import ma.ac.uir.projets8.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Create a new event", security = @SecurityRequirement(name = "bearerAuth"))
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

    @Operation(summary = "Update an event by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT')")
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody NewEventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @Operation(summary = "Delete an event by ID", security = @SecurityRequirement(name = "bearerAuth"))
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

    @Operation(summary = "get the list of transactions",
            description = "returns the associated list of transactions according to the event with the given id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT','TREASURER')")
    @GetMapping("{event_id}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionsByBudget(
            @PathVariable("event_id") Long id,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "25") Integer pageSize
    ) {
        if (pageNumber < 0 || pageSize < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request: negative page number or size");
        }

        Page<Transaction> resultPage = eventService.getTransactionsByEvent(id, pageNumber, pageSize);

        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));

        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }

    @Operation(summary = "get the list of participants",
            description = "returns the participants list of transactions according to the event with the given id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT')")
    @GetMapping("{event_id}/participants")
    public ResponseEntity<List<Student>> getParticipantsByEvent(
            @PathVariable("event_id") Long id,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "25") Integer pageSize
    ) {
        if (pageNumber < 0 || pageSize < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request: negative page number or size");
        }

        Page<Student> resultPage = eventService.getParticipantsByEvent(id,pageNumber,pageSize);

        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));

        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }

    @Operation(summary = "get all members of a club bundled in and excel file", description = "get club members in an excel file")
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT','SECRETARY')")
    @GetMapping("/{event_id}/participants/file")
    public ResponseEntity<Void> getParticipantsFile(@PathVariable("event_id") Long id, HttpServletResponse response){
        eventService.getEventParticipantsFile(id, response);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(summary = "Get filtered page of events ",
            description = "Get events Page filtered by eventStatus or keyword search matching title or description"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))}
            ),
            @ApiResponse(responseCode = "404", description = "Bad request",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))}
            )
    })
    @GetMapping()
    ResponseEntity<List<Event>> eventsByFilter(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "25") Integer pageSize,
            @RequestParam(name = "search", defaultValue = "") String searchKeyword,
            @RequestParam(name = "status", defaultValue = "REQUESTED,APPROVED,REJECTED,POST_EVENT,CLOSED") List<EventStatus> statusList
    ) {
        if (pageNumber < 0 || pageSize < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request: negative page number or size");
        }

        Page<Event> eventPage = eventService.getEventsPageFiltered(searchKeyword, pageNumber, pageSize, statusList);

        if (pageNumber > eventPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(eventPage.getTotalPages()));

        return new ResponseEntity<>(eventPage.getContent(), responseHeaders, HttpStatus.OK);
    }

    public record NewEventRequest(
            String name,
            String description,
            Date date,
            Integer organizer,
            String cover
    ) {
    }
}
