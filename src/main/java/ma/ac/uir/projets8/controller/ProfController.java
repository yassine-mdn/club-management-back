package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.model.Prof;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.repository.ProfRepository;
import ma.ac.uir.projets8.service.ProfService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profs")
public class ProfController {

    private final ProfRepository profRepository;
    private final ProfService profService;


    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public void addProf(@RequestBody NewProfRequest request) {
        profService.addProf(request);
    }
    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Prof>> getAllProfs() {
        return ResponseEntity.ok(profService.getAllProfs());
    }


    @GetMapping("{prof_id}")
    public ResponseEntity<Prof> getProfById(@PathVariable("prof_id") Integer id){
        return ResponseEntity.ok(profService.getProfById(id));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{prof_id}")
    public void updateProf(@PathVariable("prof_id") Integer id, @RequestBody NewProfRequest request) {
        profService.updateProf(id,request);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{prof_id}")
    public void deleteProf(@PathVariable("prof_id") Integer id){
        profService.deleteProf(id);
    }

    @GetMapping("/page={pageNumber}/size={size}")
    @ApiResponse(headers = {@Header(name = "total-pages",description = "the total number of pages",schema = @Schema(type = "string"))})
    public ResponseEntity<List<Prof>> getProfsPageable(
            @PathVariable Integer pageNumber,
            @PathVariable Integer size
    ) {
        return profService.getProfsPage(pageNumber, size);
    }

    public record NewProfRequest(
            String lastName,
            String firstName,
            String email,
            String password
    ) {
    }

}
