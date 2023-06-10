package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.ClubController;
import ma.ac.uir.projets8.exception.ClubNotFoundException;
import ma.ac.uir.projets8.controller.ClubController.NewClubDetailsRequest;
import ma.ac.uir.projets8.exception.PageOutOfBoundsException;
import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.model.ClubDetails;
import ma.ac.uir.projets8.repository.ClubDetailsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ClubDetailsService {

    private final ClubDetailsRepository clubDetailsRepository;
    public ResponseEntity<ClubDetails> getClubDetailsById(Integer id) {

        try {
            return ResponseEntity.ok(clubDetailsRepository.findById(id).orElseThrow(() -> new ClubNotFoundException(id)));
        } catch (ClubNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public ResponseEntity<String> updateClub(Integer id, NewClubDetailsRequest request) {
        clubDetailsRepository.findById(id).map(club -> {
                    if (!request.logo().isEmpty())
                        club.setLogo(request.logo());
                    if (!request.cover().isEmpty())
                        club.setCover(request.cover());
                    if (!request.description().isEmpty())
                        club.setDescription(request.description());
                    if (!request.email().isEmpty())
                        club.setEmail(request.email());
                    if(!request.phone().isEmpty())
                        club.setPhone(request.phone());
                    if(!request.aboutUs().isEmpty())
                        club.setAboutUs(request.aboutUs());
                    return clubDetailsRepository.save(club);
                }
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, new ClubNotFoundException(id).getMessage()));
        return new ResponseEntity<>("Club account with " + id + " successfully updated", HttpStatus.ACCEPTED);
    }

    public ResponseEntity<List<ClubDetails>> getClubsDetailsPage(Integer pageNumber, Integer size) {

        if (pageNumber < 0 || size < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Page<ClubDetails> resultPage = clubDetailsRepository.findAll(PageRequest.of(pageNumber, size));
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }
}
