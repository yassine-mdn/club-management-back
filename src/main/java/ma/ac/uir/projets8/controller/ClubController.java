package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.service.ClubService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/clubs")
public class ClubController {

    private final ClubService clubService;

    @Operation(summary = "Create a new club")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Club successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<Club> createClub(@RequestBody Club club) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clubService.createClub(club));
    }

    @Operation(summary = "Get all clubs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    @GetMapping
    public ResponseEntity<List<Club>> getAllClubs() {
        return ResponseEntity.ok(clubService.getAllClubs());
    }

    @Operation(summary = "Get a club by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Club> getClubById(@PathVariable Long id) {
        Optional<Club> optionalClub = clubService.getClubById(id);
        if (optionalClub.isPresent()) {
            return ResponseEntity.ok(optionalClub.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(summary = "Update a club")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Club successfully updated"),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Club> updateClub(@PathVariable Long id, @RequestBody Club updatedClub) {
        return ResponseEntity.ok(clubService.updateClub(updatedClub));
    }

    @Operation(summary = "Delete a club by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Club successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClub(@PathVariable Long id) {
        clubService.deleteClub(id);
        return ResponseEntity.noContent().build();
    }
}
