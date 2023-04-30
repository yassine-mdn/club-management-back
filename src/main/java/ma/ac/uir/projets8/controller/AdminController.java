package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.model.Admin;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.repository.AdminRepository;
import ma.ac.uir.projets8.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admins")
public class AdminController {

    private final AdminRepository adminRepository;
    private final AdminService adminService;


    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "create a new Admin", description = "adds an admin account to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin account successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "406", description = "Email already exists")
    })
    public ResponseEntity<String> addAdmin(@RequestBody NewAdminRequest request) {
        return adminService.addAdmin(request);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "get All Admins", description = "returns all the admin accounts ", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved")
    })
    @GetMapping
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    @Operation(summary = "get an admin account by id", description = "returns an admin account per the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @GetMapping("{admin_id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable("admin_id") Integer id) {
        return adminService.getAdminById(id);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "update an account by id", description = "updates the admin account with the specified id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully updated"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PutMapping("{admin_id}")
    public ResponseEntity<String> updateAdmin(@PathVariable("admin_id") Integer id, @RequestBody NewAdminRequest request) {
        return adminService.updateAdmin(id, request);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "delete an account with id", description = "delete the admin account with the specified id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "deleted updated"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @DeleteMapping("{admin_id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable("admin_id") Integer id) {

        return adminService.deleteAdmin(id);
    }

    @Operation(summary = "get a page of Admins", description = "returns a specific page of admin accounts with the specified number of lines")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully retrieved",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))
                    }),
            @ApiResponse(responseCode = "404", description = "invalid page number",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))},
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    @GetMapping("/page={pageNumber}/size={size}")
    public ResponseEntity<List<Admin>> getAdminsPageable(
            @PathVariable Integer pageNumber,
            @PathVariable Integer size
    ) {
        return adminService.getAdminsPage(pageNumber, size);
    }

    public record NewAdminRequest(
            String lastName,
            String firstName,
            String email,
            String password
    ) {
    }

}
