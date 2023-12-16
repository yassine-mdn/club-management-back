package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.exception.PageOutOfBoundsException;
import ma.ac.uir.projets8.model.Account;
import ma.ac.uir.projets8.model.Meeting;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.repository.MeetingRepository;
import ma.ac.uir.projets8.service.MeetingService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meetings")
public class MeetingController {

    private final MeetingRepository meetingRepository;
    private final MeetingService meetingService;


    @Operation(summary = "create a new Meeting", description = "adds a new meeting  to the database", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Meeting account successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "406", description = "Email already exists")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF')")
    @PostMapping
    public ResponseEntity<Meeting> addMeeting(@RequestBody NewMeetingRequest request) {
        
        return ResponseEntity.ok(meetingService.addMeeting(request));
    }

    @Operation(summary = "get All Meetings", description = "returns all the meetings ", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved")
    })
    @GetMapping
    public ResponseEntity<List<Meeting>> getAllMeetings() {
        
        return meetingService.getAllMeetings();
    }

    @Operation(summary = "get an meeting by id", description = "returns an meeting per the id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT','TREASURER','SECRETARY')")
    @GetMapping("{meeting_id}")
    public ResponseEntity<Meeting> getMeetingById(@PathVariable("meeting_id") Integer id){
        
        return ResponseEntity.ok(meetingService.getMeetingById(id));
    }

    @Operation(summary = "get the Organizer of the meeting", description = "returns the organizer account per the meeting id", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @GetMapping("{meeting_id}/organiser")
    public ResponseEntity<Account> getOrganiserByMeetingId(@PathVariable("meeting_id") Integer id){
        
        return meetingService.getMeetingOrganiser(id);
    }


    @Operation(summary = "get a page of the users meetings",
            description = "returns a specific page of users meetings be it organised or invited to with the specified number of lines",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully retrieved",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))
                    }),
            @ApiResponse(responseCode = "404", description = "invalid page number",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))},
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    @GetMapping("/me")
    public ResponseEntity<List<Meeting>> getMyMeetings(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "25") Integer pageSize
    ){

        if (pageNumber < 0 || pageSize < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");

        Page<Meeting> resultPage = meetingService.getMyMeetings(pageNumber,pageSize);

        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));

        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }

    @Operation(summary = "get the participants of the meeting", description = "returns the participants account list per the meeting id",deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @GetMapping("{meeting_id}/participants")
    public ResponseEntity<List<Student>> getParticipantsByMeetingId(@PathVariable("meeting_id") Integer id){
        
        return meetingService.getMeetingParticipants(id);
    }

    @Operation(summary = "update an account by id", description = "updates the meeting with the specified id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully updated"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF')")
    @PutMapping("{meeting_id}")
    public ResponseEntity<String> updateMeeting(@PathVariable("meeting_id") Integer id, @RequestBody NewMeetingRequest request) {
       
       return meetingService.updateMeeting(id,request);
    }

    @Operation(summary = "delete an Meeting by id", description = "delete the meeting with the specified id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "meeting deleted"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF')")
    @DeleteMapping("{meeting_id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable("meeting_id") Integer id){
        
        meetingService.deleteMeeting(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Operation(summary = "get a page of Meetings", description = "returns a specific page of meeting Meetings with the specified number of lines",deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully retrieved",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))
                    }),
            @ApiResponse(responseCode = "404", description = "invalid page number",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))},
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF')")
    @GetMapping("/page={pageNumber}/size={size}")
    public ResponseEntity<List<Meeting>> getMeetingsPageable(
            @PathVariable Integer pageNumber,
            @PathVariable Integer size
    ) {
        return meetingService.getMeetingsPage(pageNumber, size);
    }

    public record NewMeetingRequest(
            String title,
            Date date,
            String description,
            Integer lengthInMinutes,
            String location,
            List<Integer> participantsIds
    ) {
    }
}
