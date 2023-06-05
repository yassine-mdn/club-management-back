package ma.ac.uir.projets8.service;


import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.ClubController.NewClubRequest;
import ma.ac.uir.projets8.exception.AccountNotFoundException;
import ma.ac.uir.projets8.exception.ClubNotFoundException;
import ma.ac.uir.projets8.exception.PageOutOfBoundsException;
import ma.ac.uir.projets8.model.Budget;
import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.model.ClubDetails;
import ma.ac.uir.projets8.model.enums.ClubStatus;
import ma.ac.uir.projets8.model.enums.ClubType;
import ma.ac.uir.projets8.repository.ClubRepository;
import ma.ac.uir.projets8.repository.PersonnelRepository;
import ma.ac.uir.projets8.repository.StudentRepository;
import ma.ac.uir.projets8.util.NullChecker;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ClubService {

    private final ClubRepository clubRepository;

    private final StudentRepository studentRepository;

    private final PersonnelRepository personnelRepository;

    public ResponseEntity<List<Club>> getAllClubs() {

        return ResponseEntity.ok(clubRepository.findAll());
    }

    public ResponseEntity<String> addClub(NewClubRequest request) {

        if (!ObjectUtils.allNotNull(request.name(), request.description(), request.committeeIds()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Club club = new Club();
        club.setName(request.name());
        club.setDescription(request.description());
        club.setType(request.supervisorId() == null ? ClubType.NORMAL : ClubType.ACADEMIC);
        if (request.supervisorId() != null)
            club.addSupervisor(personnelRepository.findById(request.supervisorId()).orElseThrow(() ->  new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid supervisor id request")));
        club.addCommitteeMembers(studentRepository.findAllById(request.committeeIds()));
        club.setStatus(request.status() == null ? ClubStatus.CREATION_STEP_1 : request.status());
        club.addClubDetails(new ClubDetails());
        Budget budget = new Budget();
        budget.setClub(club);
        budget.setBudget_initial(0.0);
        budget.setBudget_restant(0.0);
        club.setBudget(budget);
        clubRepository.save(club);
        return new ResponseEntity<>("Club successfully added to database", HttpStatus.CREATED);
    }

    public ResponseEntity<Club> getClubById(Integer id) {

        try {
            return ResponseEntity.ok(clubRepository.findById(id).orElseThrow(() -> new ClubNotFoundException(id)));
        } catch (ClubNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public ResponseEntity<String> updateClub(Integer id, NewClubRequest request) {
        clubRepository.findById(id).map(club -> {
                    if (!request.name().isEmpty())
                        club.setName(request.name());
                    if (!request.description().isEmpty())
                        club.setDescription(request.description());
                    if (!request.committeeIds().isEmpty())
                        club.addCommitteeMembers(studentRepository.findAllById(request.committeeIds()));
                    if (request.supervisorId() != null)
                        club.addSupervisor(personnelRepository.findById(request.supervisorId()).orElseThrow(() ->  new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid supervisor id request")));
                    if (request.status() != null)
                        club.setStatus(request.status());
                    return clubRepository.save(club);
                }
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, new ClubNotFoundException(id).getMessage()));
        return new ResponseEntity<>("Club account with " + id + " successfully updated", HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> deleteClub(Integer id) {

        try {
            clubRepository.deleteById(id);
            return new ResponseEntity<>("Club successfully deleted", HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, new ClubNotFoundException(id).getMessage());
        }
    }

    public ResponseEntity<List<Club>> getClubsByStatus(ClubStatus status) {

        return ResponseEntity.ok(clubRepository.findAllByStatus(status));
    }

    public ResponseEntity<List<Club>> getClubsPage(Integer pageNumber, Integer size) {

        if (pageNumber < 0 || size < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Page<Club> resultPage = clubRepository.findAll(PageRequest.of(pageNumber, size));
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }
}
