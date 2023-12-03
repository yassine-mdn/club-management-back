package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.exception.ClubNotFoundException;
import ma.ac.uir.projets8.exception.PageOutOfBoundsException;
import ma.ac.uir.projets8.model.*;
import ma.ac.uir.projets8.model.enums.ClubStatus;
import ma.ac.uir.projets8.model.enums.ClubType;
import ma.ac.uir.projets8.model.enums.EventStatus;
import ma.ac.uir.projets8.repository.ClubRepository;
import ma.ac.uir.projets8.service.ClubDetailsService;
import ma.ac.uir.projets8.service.ClubService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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


    @PostMapping
    @Operation(summary = "create a new Club", description = "adds an club to the database", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Club successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "406", description = "Email already exists")
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF','STUDENT')")
    public ResponseEntity<Club> addClub(@RequestBody ClubController.NewClubRequest request) {
        return ResponseEntity.ok(clubService.addClub(request));
    }

    @Operation(summary = "get an club  by id", description = "returns an club per the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @GetMapping("{club_id}")
    public ResponseEntity<Club> getClubById(@PathVariable("club_id") Integer id) {
        return ResponseEntity.ok(clubService.getClubById(id));
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


    @Operation(summary = "get club events by id and/or search,status filter",
            description = "returns events organized by a club per the id and filtered by status or keyword search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully retrieved",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))
                    }),
            @ApiResponse(responseCode = "404", description = "invalid page number",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))},
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    @GetMapping("{club_id}/events")
    public ResponseEntity<List<Event>> getClubEvents(
            @PathVariable("club_id") Integer id,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "25") Integer pageSize,
            @RequestParam(name = "search", defaultValue = "") String searchKeyword,
            @RequestParam(name = "status", defaultValue = "REQUESTED,APPROVED,REJECTED,POST_EVENT,CLOSED") List<EventStatus> statusList
    ) {
        if (pageNumber < 0 || pageSize < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Page<Event> resultPage = clubService.getClubEvents(id, pageNumber, pageSize, searchKeyword, statusList);
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }

    @Operation(summary = "get an club members by id", description = "returns events organized by a club per the id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT','SECRETARY')")
    @GetMapping("{club_id}/members")
    public ResponseEntity<List<Student>> getClubMembers(
            @PathVariable("club_id") Integer id,
            @RequestParam(name = "pageNumber") Integer pageNumber,
            @RequestParam(name = "pageSize") Integer pageSize) {


        if (pageNumber < 0 || pageSize < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Page<Student> resultPage = clubService.getClubMembers(id, pageNumber, pageSize);
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }


    @Operation(summary = "update a club by id", description = "updates the club with the specified id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully updated"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF')")
    @PutMapping("{club_id}")
    public ResponseEntity<Club> updateClub(@PathVariable("club_id") Integer id, @RequestBody NewClubRequest request) {
        return ResponseEntity.ok(clubService.updateClub(id, request));
    }

    @Operation(summary = "update club details by id", description = "updates the club details of the clubwith the specified id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully updated"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT','SECRETARY')")
    @PutMapping("{club_id}/details")
    public ResponseEntity<String> updateClubDetails(@PathVariable("club_id") Integer id, @RequestBody NewClubDetailsRequest request) {
        return clubDetailsService.updateClub(id, request);
    }

    @Operation(summary = "delete a club with id", description = "delete the club with the specified id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "deleted updated"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT','SECRETARY')")
    @DeleteMapping("{club_id}")
    public ResponseEntity<Void> deleteClub(@PathVariable("club_id") Integer id) {
        clubService.deleteClub(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "25") Integer pageSize,
            @RequestParam(name = "search", defaultValue = "") String searchKeyWord,
            @RequestParam(
                    name = "status",
                    defaultValue = "CREATION_STEP_1,CREATION_STEP_2,CREATION_STEP_3,ACTIVE,ABANDONED,DECLINED") List<ClubStatus> clubStatuses,
            @RequestParam(name = "type", defaultValue = "NORMAL,ACADEMIC") List<ClubType> clubTypes
    ) {
        if (pageNumber < 0 || pageSize < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Page<Club> resultPage = clubService.getCubsPageFiltered(searchKeyWord, pageNumber, pageSize, clubStatuses, clubTypes);
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }

    @Operation(summary = "get Page Clubs with specified status", description = "returns page of clubs with specified status ", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully retrieved",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))
                    }),
            @ApiResponse(responseCode = "404", description = "invalid page number",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))},
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Club>> getAllClubsWithStatus(
            @PathVariable("status") ClubStatus status,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "25") Integer pageSize
    ) {
        if (pageNumber < 0 || pageSize < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Page<Club> resultPage = clubService.getClubsByStatus(status, pageNumber, pageSize);
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
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
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "25") Integer pageSize
    ) {

        if (pageNumber < 0 || pageSize < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Page<ClubDetails> resultPage = clubDetailsService.getClubsDetailsPage(pageNumber, pageSize);
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);

    }

    @Deprecated
    @PostMapping("/{club_id}/members")
    public ResponseEntity<Void> batchAddClubs(@RequestParam("file") MultipartFile file, @PathVariable("club_id") Integer id) throws IOException {
        clubService.addMembersFromFile(file, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "get all members of a club bundled in and excel file", description = "get club members in an excel file")
    @PreAuthorize("hasAnyRole('ADMIN','PROF','PRESIDENT','VICE_PRESIDENT','SECRETARY')")
    @GetMapping("/{club_id}/members/file")
    public void getClubMembersFile(@PathVariable("club_id") Integer id, HttpServletResponse response) {

        clubService.getClubMembersFile(id, response);
    }

    @Operation(summary = "get a page of pending clubs",
            description = "returns a specific page of pending clubs with the specified number of lines",
            security = @SecurityRequirement(name = "bearerAuth"),
            deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully retrieved",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))
                    }),
            @ApiResponse(responseCode = "404", description = "invalid page number",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))},
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    @PreAuthorize("hasAnyRole('ADMIN','PROF')")
    @GetMapping("/pending")
    public ResponseEntity<List<Club>> getPendingClubs(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "25") Integer pageSize) {
        return clubService.getPendingClubs(pageNumber, pageSize);
    }


    @Operation(summary = "get a page of featured clubs",
            description = "returns a specific page of featured clubs with the specified number of lines",
            security = @SecurityRequirement(name = "bearerAuth"))

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
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "25") Integer pageSize) {
        return clubService.getFeaturedClubs(pageNumber, pageSize);
    }

    @Operation(summary = "get the budgets of a club ",
            description = "returns the budgets of a club with the specified id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "invalid club id"),
    })
    @GetMapping("/{club_id}/budgets")
    public ResponseEntity<List<Budget>> getClubBudgets(
            @PathVariable("club_id") Integer id) {
        return ResponseEntity.ok(clubService.getClubBudgets(id));
    }

    @Operation(summary = "get all managed clubs",
            description = "returns all clubs managed by the authenticated user",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "invalid page number or size"),
    })
    @GetMapping("/managed")
    public ResponseEntity<Page<Club>> getManagedClubs(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "25") Integer pageSize
    ) {
        if (pageNumber < 0 || pageSize < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        return ResponseEntity.ok(clubService.getMyClubs(pageNumber, pageSize));
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
