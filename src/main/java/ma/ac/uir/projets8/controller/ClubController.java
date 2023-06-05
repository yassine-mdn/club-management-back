package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.model.ClubDetails;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.model.enums.ClubStatus;
import ma.ac.uir.projets8.model.enums.ClubType;
import ma.ac.uir.projets8.repository.ClubRepository;
import ma.ac.uir.projets8.service.ClubDetailsService;
import ma.ac.uir.projets8.service.ClubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clubs")
public class ClubController {


    private final ClubRepository clubRepository;
    private final ClubService clubService;
    private final ClubDetailsService clubDetailsService;


    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "create a new Club", description = "adds an club to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Club successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "406", description = "Email already exists")
    })
    public ResponseEntity<String> addClub(@RequestBody ClubController.NewClubRequest request) {
        return clubService.addClub(request);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "get All Clubs", description = "returns all the club ", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved")
    })
    @GetMapping
    public ResponseEntity<List<Club>> getAllClubs() {
        return clubService.getAllClubs();
    }

    @Operation(summary = "get an club  by id", description = "returns an club per the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @GetMapping("{club_id}")
    public ResponseEntity<Club> getClubById(@PathVariable("club_id") Integer id) {
        return clubService.getClubById(id);
    }


    @Operation(summary = "get an club details by id", description = "returns details of a club per the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @GetMapping("{club_id}/details")
    public ResponseEntity<ClubDetails> getClubDetailsById(@PathVariable("club_id") Integer id) {
        return clubDetailsService.getClubDetailsById(id);
    }


    @Operation(summary = "get All Clubs with specified status", description = "returns all the club with specified status ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved")
    })
    @GetMapping("/status/{status}") 
    public ResponseEntity<List<Club>> getAllClubsWithStatus(@PathVariable("status") ClubStatus status) {
        return clubService.getClubsByStatus(status);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "update a club by id", description = "updates the club with the specified id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully updated"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PutMapping("{club_id}")
    public ResponseEntity<String> updateClub(@PathVariable("club_id") Integer id, @RequestBody NewClubRequest request) {
        return clubService.updateClub(id, request);
    }

    @Operation(summary = "update club details by id", description = "updates the club details of the clubwith the specified id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully updated"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PutMapping("{club_id}/details")
    public ResponseEntity<String> updateClubDetails(@PathVariable("club_id") Integer id, @RequestBody NewClubDetailsRequest request) {
        return clubDetailsService.updateClub(id, request);
    }


    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "delete a club with id", description = "delete the club with the specified id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "deleted updated"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @DeleteMapping("{club_id}")
    public ResponseEntity<String> deleteClub(@PathVariable("club_id") Integer id) {

        return clubService.deleteClub(id);
    }

    @Operation(summary = "get a page of Clubs", description = "returns a specific page of clubs with the specified number of lines")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully retrieved",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))
                    }),
            @ApiResponse(responseCode = "404", description = "invalid page number",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))},
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    @GetMapping("/page={pageNumber}/size={size}")
    public ResponseEntity<List<Club>> getClubsPageable(
            @PathVariable Integer pageNumber,
            @PathVariable Integer size
    ) {
        return clubService.getClubsPage(pageNumber, size);
    }

    public record NewClubRequest(
            String name,
            String description,
            Set<Integer> committeeIds,
            ClubType type,
            ClubStatus status
    ) {
    }

    public record NewClubDetailsRequest(
            String logo,
            String cover,
            String description,
            String email,
            String phone,
            String aboutUs,
            List<String> socials,
            List<String> medias
           
    ) {
    }
}