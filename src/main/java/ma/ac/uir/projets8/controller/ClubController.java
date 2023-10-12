package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.model.ClubDetails;
import ma.ac.uir.projets8.model.Event;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.model.enums.ClubStatus;
import ma.ac.uir.projets8.model.enums.ClubType;
import ma.ac.uir.projets8.repository.ClubRepository;
import ma.ac.uir.projets8.service.ClubDetailsService;
import ma.ac.uir.projets8.service.ClubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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


    @Operation(summary = "get an club events by id", description = "returns events organized by a club per the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @GetMapping("{club_id}/events")
    public ResponseEntity<List<Event>> getClubEvents(@PathVariable("club_id") Integer id) {
        return clubService.getClubEvents(id);
    }

    @Operation(summary = "get an club members by id", description = "returns events organized by a club per the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @GetMapping("{club_id}/members")
    public ResponseEntity<List<Student>> getClubMembers(
            @PathVariable("club_id") Integer id,
            @RequestParam(name = "pageNumber") Integer pageNumber,
            @RequestParam(name = "pageSize") Integer size) {
        return clubService.getClubMembers(id, pageNumber, size);
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

    @Operation(summary = "get a page of Clubs with keyword search", description = "returns a specific page of clubs with the specified number of lines")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully retrieved",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))
                    }),
            @ApiResponse(responseCode = "404", description = "invalid page number",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))},
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    @GetMapping
    public ResponseEntity<List<Club>> getClubsPageable(
            @RequestParam(name = "pageNumber") Integer pageNumber,
            @RequestParam(name = "pageSize") Integer size,
            @RequestParam(name = "search", required = false) String searchKeyWord
    ) {
        if (searchKeyWord != null)
            return clubService.getCubsPageBySearch(searchKeyWord, pageNumber, size);
        else
            return clubService.getClubsPage(pageNumber, size);

    }


    @Operation(summary = "get a page of Clubs with extra details", description = "returns a specific page of clubs with the specified number of lines")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully retrieved",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))
                    }),
            @ApiResponse(responseCode = "404", description = "invalid page number",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))},
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    @GetMapping("/detailed")
    public ResponseEntity<List<ClubDetails>> getClubsDetailsPageable(
            @RequestParam(name = "pageNumber") Integer pageNumber,
            @RequestParam(name = "pageSize") Integer size
    ) {

        return clubDetailsService.getClubsDetailsPage(pageNumber, size);

    }

    @Deprecated
    @PostMapping("/{club_id}/members")
    public ResponseEntity<String> batchAddClubs(@RequestParam("file") MultipartFile file, @PathVariable("club_id") Integer id) throws IOException {
        return ResponseEntity.ok(clubService.addMembersFromFile(file, id));
    }

    @Operation(summary = "get all members of a club bundled in and excel file", description = "get club members in an excel file")
    @GetMapping("/{club_id}/members/file")
    public void getClubMembersFile(@PathVariable("club_id") Integer id, HttpServletResponse response) {

        clubService.getClubMembersFile(id, response);
    }

    @Operation(summary = "get a page of pending clubs", description = "returns a specific page of pending clubs with the specified number of lines")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully retrieved",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))
                    }),
            @ApiResponse(responseCode = "404", description = "invalid page number",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))},
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    @GetMapping("/pending")
    public ResponseEntity<List<Club>> getPendingClubs(
            @RequestParam(name = "pageNumber") Integer pageNumber,
            @RequestParam(name = "pageSize") Integer size) {
        return clubService.getPendingClubs(pageNumber, size);
    }


    @Operation(summary = "get a page of featured clubs", description = "returns a specific page of featured clubs with the specified number of lines")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully retrieved",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))
                    }),
            @ApiResponse(responseCode = "404", description = "invalid page number",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))},
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    @GetMapping("/featured")
    public ResponseEntity<List<Club>> getFeaturedClubs(
            @RequestParam(name = "pageNumber") Integer pageNumber,
            @RequestParam(name = "pageSize") Integer size) {
        return clubService.getFeaturedClubs(pageNumber, size);
    }

    public record NewClubRequest(
            String name,
            String description,
            Integer supervisorId,//Nullable
            Set<Integer> committeeIds,
            ClubStatus status,
            Boolean featured
    ) {
    }

    public record NewClubDetailsRequest(
            String logo,
            String cover,
            String description,
            String email,
            String phone,
            String aboutUs

    ) {
    }
}
