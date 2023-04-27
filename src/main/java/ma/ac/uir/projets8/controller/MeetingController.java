package ma.ac.uir.projets8.controller;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.model.Admin;
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
