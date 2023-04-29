package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.model.Admin;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.repository.AdminRepository;
import ma.ac.uir.projets8.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admins")
public class AdminController {

    private final AdminRepository adminRepository;
    private final AdminService adminService;


    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public void addAdmin(@RequestBody NewAdminRequest request) {
       adminService.addAdmin(request);
    }
    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }


    @GetMapping("{admin_id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable("admin_id") Integer id){
        return ResponseEntity.ok(adminService.getAdminById(id));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{admin_id}")
    public void updateAdmin(@PathVariable("admin_id") Integer id, @RequestBody NewAdminRequest request) {
            adminService.updateAdmin(id,request);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{admin_id}")
    public void deleteAdmin(@PathVariable("admin_id") Integer id){
        adminService.deleteAdmin(id);
    }

    @GetMapping("/page={pageNumber}/size={size}")
    @ApiResponse(headers = {@Header(name = "total-pages",description = "the total number of pages",schema = @Schema(type = "string"))})
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
