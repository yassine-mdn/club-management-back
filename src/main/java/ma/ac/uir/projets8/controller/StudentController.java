package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.exception.PageOutOfBoundsException;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.model.enums.Role;
import ma.ac.uir.projets8.repository.StudentRepository;
import ma.ac.uir.projets8.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final StudentService studentService;


    @Operation(summary = "create a new Student", description = "adds a student account to the database",deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Student account successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "406", description = "Email already exists")
    })
    @PostMapping
    public ResponseEntity<String> addStudent(@RequestBody NewStudentRequest request) {

        return studentService.addStudent(request);
    }


    @Operation(summary = "get an student account by id",
            description = "returns an student account per the id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @GetMapping("{student_id}")
    public ResponseEntity<Student> getStudentById(@PathVariable("student_id") Integer id) {

        return studentService.getStudentById(id);
    }


    @Operation(summary = "update an account by id", description = "updates the student account with the specified id",deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully updated"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @PutMapping("{student_id}")
    public void updateStudent(@PathVariable("student_id") Integer id, @RequestBody NewStudentRequest request) {

        studentService.updateStudent(id, request);
    }

    @Operation(summary = "delete an account with id", description = "delete the student account with the specified id",deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "deleted updated"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @DeleteMapping("{student_id}")
    public ResponseEntity<String> deleteStudent(@PathVariable("student_id") Integer id) {

        return studentService.deleteStudent(id);
    }

    @Operation(
            summary = "get a page of Students serached by keyword",
            description = "returns a specific page of student accounts with the specified number of lines"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully retrieved",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))
                    }),
            @ApiResponse(responseCode = "404", description = "invalid page number",
                    headers = {@Header(name = "total-pages", description = "the total number of pages", schema = @Schema(type = "string"))},
                    content = @Content(schema = @Schema(implementation = Void.class))),
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<Student>> getStudentsPageable(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "25") Integer pageSize,
            @RequestParam(name = "search", defaultValue = "") String searchKeyWord
    ) {
        if (pageNumber < 0 || pageSize < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        Page<Student> resultPage = studentService.getStudentsFiltered(searchKeyWord, pageNumber, pageSize);
        if (pageNumber > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }


    public record NewStudentRequest(
            String lastName,
            String firstName,
            String email,
            String password,
            String major,
            Integer level,
            Role role
    ) {
    }

}
