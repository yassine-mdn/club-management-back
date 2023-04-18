package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.ProfController;
import ma.ac.uir.projets8.exception.CompteNotFoundException;
import ma.ac.uir.projets8.model.Prof;
import ma.ac.uir.projets8.repository.ProfRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static ma.ac.uir.projets8.model.enums.Role.ADMIN;
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

    public Prof getProfById(Integer id){
        return profRepository.findById(id).orElseThrow(() -> new CompteNotFoundException(id));
    }

    public void updateProf(Integer id, ProfController.NewProfRequest request) {
        profRepository.findById(id)
                .map(prof -> {
                            prof.setFirstName(request.firstName());
                            prof.setLastName(request.lastName());
                            prof.setEmail(request.email());
                            prof.setPassword(request.password());
                            return profRepository.save(prof);
                        }
                );
        //TODO:Add case of recieving an invalid id
    }

    public void deleteProf(Integer id){

        profRepository.deleteById(id);
    }
}
