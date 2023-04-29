package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.model.Account;
import ma.ac.uir.projets8.model.Meeting;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.repository.MeetingRepository;
import ma.ac.uir.projets8.service.MeetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meetings")
public class MeetingController {

    private final MeetingRepository meetingRepository;
    private final MeetingService meetingService;


    @PostMapping
    public void addMeeting(@RequestBody NewMeetingRequest request) {
        meetingService.addMeeting(request);
    }

    @GetMapping
    public ResponseEntity<List<Meeting>> getAllMeetings() {
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }

    @GetMapping("{meeting_id}")
    public ResponseEntity<Meeting> getMeetingById(@PathVariable("meeting_id") Integer id){
        return ResponseEntity.ok(meetingService.getMeetingById(id));
    }

    @GetMapping("{meeting_id}/organiser")
    public ResponseEntity<Account> getOrganiserByMeetingId(@PathVariable("meeting_id") Integer id){
        return ResponseEntity.ok(meetingService.getMeetingOrganiser(id));
    }

    @GetMapping("{meeting_id}/participants")
    public ResponseEntity<List<Student>> getParticipantsByMeetingId(@PathVariable("meeting_id") Integer id){
        return ResponseEntity.ok(meetingService.getMeetingParticipants(id));
    }

    @PutMapping("{meeting_id}")
    public void updateMeeting(@PathVariable("meeting_id") Integer id, @RequestBody NewMeetingRequest request) {
        meetingService.updateMeeting(id,request);
    }

    @DeleteMapping("{meeting_id}")
    public void deleteMeeting(@PathVariable("meeting_id") Integer id){
        meetingService.deleteMeeting(id);
    }

    @GetMapping("/page={pageNumber}/size={size}")
    @ApiResponse(headers = {@Header(name = "total-pages",description = "the total number of pages",schema = @Schema(type = "string"))})
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
            Integer organiserId,
            List<Integer> participantsIds
    ) {
    }
}
