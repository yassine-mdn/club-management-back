package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.ProfController.*;
import ma.ac.uir.projets8.exception.AccountNotFoundException;
import ma.ac.uir.projets8.exception.PageOutOfBoundsException;
import ma.ac.uir.projets8.model.Prof;
import ma.ac.uir.projets8.repository.ProfRepository;
import ma.ac.uir.projets8.util.NullChecker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static ma.ac.uir.projets8.model.enums.Role.ADMIN;

@RequiredArgsConstructor
@Service
public class ProfService {

    private final ProfRepository profRepository;

    public ResponseEntity<List<Prof>> getAllProfs() {
        return ResponseEntity.ok(profRepository.findAll());
    }

    public ResponseEntity<String> addProf(@RequestBody NewProfRequest request) {

        if (NullChecker.hasNull(request))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        if (profRepository.existsByEmail(request.email()))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Email already exists");
        Prof prof = new Prof();
        prof.setLastName(request.lastName());
        prof.setFirstName(request.firstName());
        prof.setEmail(request.email());
        prof.setPassword(request.password());
        prof.setRoles(List.of(ADMIN));
        profRepository.save(prof);
        return new ResponseEntity<>("Prof account successfully created", HttpStatus.CREATED);
    }

    public ResponseEntity<Prof> getProfById(Integer id) {
        try {
            return ResponseEntity.ok(profRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id)));
        } catch (AccountNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    public ResponseEntity<String> updateProf(Integer id, NewProfRequest request) {
        profRepository.findById(id)
                .map(prof -> {
                            if (!request.firstName().isEmpty())
                                prof.setFirstName(request.firstName());
                            if (!request.lastName().isEmpty())
                                prof.setLastName(request.lastName());
                            if (!request.email().isEmpty())
                                prof.setEmail(request.email());
                            if (!request.password().isEmpty())
                                prof.setPassword(request.password());
                            return profRepository.save(prof);
                        }
                ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, new AccountNotFoundException(id).getMessage()));
        return new ResponseEntity<>("Prof account with " + id + " successfully updated", HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> deleteProf(Integer id) {
        try {

            profRepository.deleteById(id);
            return ResponseEntity.ok("deleted successfully");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, new AccountNotFoundException(id).getMessage());
        }
    }

    public ResponseEntity<List<Prof>> getProfsPage(int pageNumber, int size) {
        Page<Prof> resultPage = profRepository.findAll(PageRequest.of(pageNumber, size));
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }

}
