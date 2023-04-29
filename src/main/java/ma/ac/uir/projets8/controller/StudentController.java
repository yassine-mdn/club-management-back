package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.repository.StudentRepository;
import ma.ac.uir.projets8.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final StudentService studentService;


    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public void addStudent(@RequestBody NewStudentRequest request) {
        studentService.addStudent(request);
    }
    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }


    @GetMapping("{student_id}")
    public ResponseEntity<Student> getStudentById(@PathVariable("student_id") Integer id){
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{student_id}")
    public void updateStudent(@PathVariable("student_id") Integer id, @RequestBody NewStudentRequest request) {
        studentService.updateStudent(id,request);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{student_id}")
    public void deleteStudent(@PathVariable("student_id") Integer id){
        studentService.deleteStudent(id);
    }

    @GetMapping("/page={pageNumber}/size={size}")
    @ApiResponse(headers = {@Header(name = "total-pages",description = "the total number of pages",schema = @Schema(type = "string"))})
    public ResponseEntity<List<Student>> getStudentPageable(
            @PathVariable Integer pageNumber,
            @PathVariable Integer size
    ) {
        return studentService.getStudentPage(pageNumber, size);
    }


    public record NewStudentRequest(
            String lastName,
            String firstName,
            String email,
            String password,
            String role
    ) {
    }

}
