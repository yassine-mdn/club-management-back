package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.PersonnelController.*;
import ma.ac.uir.projets8.exception.AccountNotFoundException;
import ma.ac.uir.projets8.exception.PageOutOfBoundsException;
import ma.ac.uir.projets8.model.Personnel;
import ma.ac.uir.projets8.repository.PersonnelRepository;
import ma.ac.uir.projets8.util.NullChecker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static ma.ac.uir.projets8.model.enums.Role.ADMIN;

@RequiredArgsConstructor
@Service
public class PersonnelService {

    private final PersonnelRepository personnelRepository;

    public ResponseEntity<List<Personnel>> getAllPersonnels() {

        return ResponseEntity.ok(personnelRepository.findAll());
    }

    public ResponseEntity<String> addPersonnel(NewPersonnelRequest request) {

        if (NullChecker.hasNull(request))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        if (personnelRepository.existsByEmail(request.email()))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Email already exists");
        Personnel personnel = new Personnel();
        personnel.setLastName(request.lastName());
        personnel.setFirstName(request.firstName());
        personnel.setEmail(request.email());
        personnel.setPassword(request.password());
        personnel.setRoles(List.of(request.role()));
        personnelRepository.save(personnel);
        return new ResponseEntity<>("Personnel account successfully created", HttpStatus.CREATED);
    }

    public ResponseEntity<Personnel> getPersonnelById(Integer id) {
        try {
            return ResponseEntity.ok(personnelRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id)));
        } catch (AccountNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    public ResponseEntity<String> updatePersonnel(Integer id, NewPersonnelRequest request) {
        personnelRepository.findById(id)
                .map(personnel -> {
                            if (!request.firstName().isEmpty())
                                personnel.setFirstName(request.firstName());
                            if (!request.lastName().isEmpty())
                                personnel.setLastName(request.lastName());
                            if (!request.email().isEmpty())
                                personnel.setEmail(request.email());
                            if (!request.password().isEmpty())
                                personnel.setPassword(request.password());
                            return personnelRepository.save(personnel);
                        }
                ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, new AccountNotFoundException(id).getMessage()));
        return new ResponseEntity<>("Personnel account with " + id + " successfully updated", HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> deletePersonnel(Integer id) {
        try {

            personnelRepository.deleteById(id);
            return ResponseEntity.ok("deleted successfully");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, new AccountNotFoundException(id).getMessage());
        }
    }

    public ResponseEntity<List<Personnel>> getPersonnelsPage(int pageNumber, int size) {
        if (pageNumber < 0 || size < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Page<Personnel> resultPage = personnelRepository.findAll(PageRequest.of(pageNumber, size));
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }

}
