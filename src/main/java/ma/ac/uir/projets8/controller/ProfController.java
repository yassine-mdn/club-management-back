package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.model.Prof;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.repository.ProfRepository;
import ma.ac.uir.projets8.service.ProfService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profs")
public class ProfController {

    private final ProfRepository profRepository;
    private final ProfService profService;


    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "create a new Prof", description = "adds an prof account to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Prof account successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "406", description = "Email already exists")
    })
    public ResponseEntity<String> addProf(@RequestBody NewProfRequest request) {
        return profService.addProf(request);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "get All Profs", description = "returns all the prof accounts ", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved")
    })
    @GetMapping
    public ResponseEntity<List<Prof>> getAllProfs() {
        return profService.getAllProfs();
    }

    @Operation(summary = "get an prof account by id", description = "returns an prof account per the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @GetMapping("{prof_id}")
    public ResponseEntity<Prof> getProfById(@PathVariable("prof_id") Integer id) {
        return profService.getProfById(id);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "update an account by id", description = "updates the prof account with the specified id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully updated"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PutMapping("{prof_id}")
    public ResponseEntity<String> updateProf(@PathVariable("prof_id") Integer id, @RequestBody NewProfRequest request) {
        return profService.updateProf(id, request);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "delete an account with id", description = "delete the prof account with the specified id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "deleted updated"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @DeleteMapping("{prof_id}")
    public ResponseEntity<String> deleteProf(@PathVariable("prof_id") Integer id) {

        return profService.deleteProf(id);
    }

    @Operation(summary = "get a page of Profs", description = "returns a specific page of prof accounts with the specified number of lines")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully retrieved",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))
                    }),
            @ApiResponse(responseCode = "404", description = "invalid page number",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))},
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    @GetMapping("/page={pageNumber}/size={size}")
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
