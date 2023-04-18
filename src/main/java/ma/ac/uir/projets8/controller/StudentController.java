package ma.ac.uir.projets8.controller;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.exception.CompteNotFoundException;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.repository.StudentRepository;
import ma.ac.uir.projets8.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ma.ac.uir.projets8.model.enums.Role.ADMIN;

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

    public record NewStudentRequest(
            String lastName,
            String firstName,
            String email,
            String password,
            String role
    ) {
    }

}
