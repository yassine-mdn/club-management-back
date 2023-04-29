package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.ProfController;
import ma.ac.uir.projets8.exception.AccountNotFoundException;
import ma.ac.uir.projets8.exception.PageOutOfBoundsException;
import ma.ac.uir.projets8.model.Meeting;
import ma.ac.uir.projets8.model.Prof;
import ma.ac.uir.projets8.repository.ProfRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static ma.ac.uir.projets8.model.enums.Role.PROF;

@RequiredArgsConstructor
@Service
public class ProfService {

    private final ProfRepository profRepository;

    public List<Prof> getAllProfs() {
        return profRepository.findAll();
    }

    public void addProf(@RequestBody ProfController.NewProfRequest request) {
        Prof prof = new Prof();
        prof.setLastName(request.lastName());
        prof.setFirstName(request.firstName());
        prof.setEmail(request.email());
        prof.setPassword(request.password());
        prof.setRoles(List.of(PROF));
        profRepository.save(prof);
    }

    public Prof getProfById(Integer id) {
        return profRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    public void updateProf(Integer id, ProfController.NewProfRequest request) {
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
                );
        //TODO:Add case of recieving an invalid id
    }

    public void deleteProf(Integer id) {

        profRepository.deleteById(id);
    }

    public ResponseEntity<List<Prof>> getProfsPage(int pageNumber, int size) {
        Page<Prof> resultPage = profRepository.findAll(PageRequest.of(pageNumber, size));
        if (pageNumber > resultPage.getTotalPages()) {
            throw new PageOutOfBoundsException(pageNumber);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }

}
