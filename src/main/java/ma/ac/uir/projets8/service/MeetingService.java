package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.MeetingController;
import ma.ac.uir.projets8.exception.AccountNotFoundException;
import ma.ac.uir.projets8.exception.MeetingNotFoundException;
import ma.ac.uir.projets8.exception.PageOutOfBoundsException;
import ma.ac.uir.projets8.model.Account;
import ma.ac.uir.projets8.model.Meeting;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.repository.AccountRepository;
import ma.ac.uir.projets8.repository.MeetingRepository;
import ma.ac.uir.projets8.repository.StudentRepository;
import ma.ac.uir.projets8.util.NullChecker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final AccountRepository accountRepository;

    private final StudentRepository studentRepository;

    public ResponseEntity<List<Meeting>> getAllMeetings() {

        return ResponseEntity.ok(meetingRepository.findAll());
    }

    public ResponseEntity<String> addMeeting(@RequestBody MeetingController.NewMeetingRequest request) {

        if (NullChecker.hasNull(request))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Meeting meeting = new Meeting();
        meeting.setTitle(request.title());
        meeting.setDate(request.date());
        meeting.setDescription(request.description());
        meeting.setLengthInMinutes(request.lengthInMinutes());
        meeting.setOrganiser(accountRepository.findById(request.organiserId()).orElseThrow(() -> new AccountNotFoundException(request.organiserId())));
        meeting.setParticipants(new HashSet<>(studentRepository.findAllById(request.participantsIds())));
        meetingRepository.save(meeting);
        return new ResponseEntity<>("Meeting successfully created", HttpStatus.CREATED);
    }

    public ResponseEntity<Meeting> getMeetingById(Integer id) {

        try {
            return ResponseEntity.ok(meetingRepository.findById(id).orElseThrow(() -> new MeetingNotFoundException(id)));
        } catch (MeetingNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public ResponseEntity<String>  updateMeeting(Integer id, MeetingController.NewMeetingRequest request) {

        meetingRepository.findById(id)
                .map(meeting -> {
                            if (!request.title().isEmpty())
                                meeting.setTitle(request.title());
                            if (request.date() != null)
                                meeting.setDate(request.date());
                            if (!request.description().isEmpty())
                                meeting.setDescription(request.description());
                            if (request.lengthInMinutes() != null)
                                meeting.setLengthInMinutes(request.lengthInMinutes());
                            if (request.organiserId() != null)
                                meeting.setOrganiser(accountRepository.findById(request.organiserId())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, new AccountNotFoundException(id).getMessage())));
                            if (!request.participantsIds().isEmpty())
                                meeting.setParticipants(new HashSet<>(studentRepository.findAllById(request.participantsIds())));
                            return meetingRepository.save(meeting);
                        }
                ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, new MeetingNotFoundException(id).getMessage()));
        ;
        return new ResponseEntity<>("Prof account with " + id + " successfully updated", HttpStatus.ACCEPTED);
    }

    public ResponseEntity<List<Student>> getMeetingParticipants(Integer id) {

        try {
            return ResponseEntity.ok(new ArrayList<>(meetingRepository.findById(id).orElseThrow(() -> new MeetingNotFoundException(id)).getParticipants()));
        }catch (MeetingNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public ResponseEntity<Account> getMeetingOrganiser(Integer id) {

        try {
            return ResponseEntity.ok(meetingRepository.findById(id).orElseThrow(() -> new MeetingNotFoundException(id)).getOrganiser());
        }catch (MeetingNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public ResponseEntity<String> deleteMeeting(Integer id) {

        try {
            meetingRepository.deleteById(id);
            return ResponseEntity.ok("deleted successfully");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, new MeetingNotFoundException(id).getMessage());
        }

    }

    public ResponseEntity<List<Meeting>> getMeetingsPage(int pageNumber, int size) {
        Page<Meeting> resultPage = meetingRepository.findAll(PageRequest.of(pageNumber, size));
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }


}
