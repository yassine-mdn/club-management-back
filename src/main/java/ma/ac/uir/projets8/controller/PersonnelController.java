package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.model.Personnel;
import ma.ac.uir.projets8.model.enums.Role;
import ma.ac.uir.projets8.repository.PersonnelRepository;
import ma.ac.uir.projets8.service.PersonnelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/personnels")
public class PersonnelController {

    private final PersonnelRepository personnelRepository;
    private final PersonnelService personnelService;


    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "create a new Personnel", description = "adds an personnel account to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Personnel account successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "406", description = "Email already exists")
    })
    public ResponseEntity<String> addPersonnel(@RequestBody NewPersonnelRequest request) {
        return personnelService.addPersonnel(request);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "get All Personnels", description = "returns all the personnel accounts ", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved")
    })
    @GetMapping
    public ResponseEntity<List<Personnel>> getAllPersonnels() {
        return personnelService.getAllPersonnels();
    }

    @Operation(summary = "get an personnel account by id", description = "returns an personnel account per the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @GetMapping("{personnel_id}")
    public ResponseEntity<Personnel> getPersonnelById(@PathVariable("personnel_id") Integer id) {
        return personnelService.getPersonnelById(id);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "update an account by id", description = "updates the personnel account with the specified id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully updated"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PutMapping("{personnel_id}")
    public ResponseEntity<String> updatePersonnel(@PathVariable("personnel_id") Integer id, @RequestBody NewPersonnelRequest request) {
        return personnelService.updatePersonnel(id, request);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "delete an account with id", description = "delete the personnel account with the specified id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "deleted updated"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @DeleteMapping("{personnel_id}")
    public ResponseEntity<String> deletePersonnel(@PathVariable("personnel_id") Integer id) {

        return personnelService.deletePersonnel(id);
    }

    @Operation(summary = "get the managed clubs", description = "returns the lsit of clubs managed by the personnel account with the specified id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @GetMapping("{personnel_id}/clubs")
    public ResponseEntity<List<Club>> getManagedClubs(@PathVariable("personnel_id") Integer id) {
        return personnelService.getClubsByPersonnelId(id);
    }

    @Operation(summary = "get a page of Personnels", description = "returns a specific page of personnel accounts with the specified number of lines")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully retrieved",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))
                    }),
            @ApiResponse(responseCode = "404", description = "invalid page number",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))},
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    @GetMapping("/page={pageNumber}/size={size}")
    public ResponseEntity<List<Personnel>> getPersonnelsPageable(
            @PathVariable Integer pageNumber,
            @PathVariable Integer size
    ) {
        return personnelService.getPersonnelsPage(pageNumber, size);
    }

    public record NewPersonnelRequest(
            String lastName,
            String firstName,
            String email,
            Role role,
            String password
    ) {
    }

}
