package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.StudentController;
import ma.ac.uir.projets8.exception.AccountNotFoundException;
import ma.ac.uir.projets8.exception.PageOutOfBoundsException;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.model.enums.Role;
import ma.ac.uir.projets8.repository.StudentRepository;
import ma.ac.uir.projets8.util.NullChecker;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static ma.ac.uir.projets8.model.enums.Role.STUDENT;

@RequiredArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentRepository.findAll());
    }

    public ResponseEntity<String> addStudent(@RequestBody StudentController.NewStudentRequest request) {
        if (!ObjectUtils.allNotNull(request.firstName(), request.lastName(), request.email(), request.password()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        if (studentRepository.existsByEmail(request.email()))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Email already exists");
        Student student = new Student();
        student.setLastName(request.lastName());
        student.setFirstName(request.firstName());
        student.setEmail(request.email());
        student.setPassword(request.password());
        if (request.role() == null)
            student.setRoles(List.of(STUDENT));
        else
            student.setRoles(List.of(STUDENT, request.role()));
        studentRepository.save(student);
        return new ResponseEntity<>("Student account successfully created", HttpStatus.CREATED);
    }

    public ResponseEntity<Student> getStudentById(Integer id) {
        try {
            return ResponseEntity.ok(studentRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id)));
        } catch (AccountNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public ResponseEntity<String> updateStudent(Integer id, StudentController.NewStudentRequest request) {
        studentRepository.findById(id)
                .map(student -> {
                            if (!request.firstName().isEmpty())
                                student.setFirstName(request.firstName());
                            if (!request.lastName().isEmpty())
                                student.setLastName(request.lastName());
                            if (!request.email().isEmpty())
                                student.setEmail(request.email());
                            if (!request.password().isEmpty())
                                student.setPassword(request.password());
                            if (request.role() != null)
                                student.setRoles(List.of(STUDENT, request.role()));
                            return studentRepository.save(student);
                        }
                ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, new AccountNotFoundException(id).getMessage()));
        return new ResponseEntity<>("Student account with " + id + " successfully updated", HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> deleteStudent(Integer id) {

        try {
            studentRepository.deleteById(id);
            return ResponseEntity.ok("deleted successfully");
        }   catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, new AccountNotFoundException(id).getMessage());
        }
    }

    public ResponseEntity<List<Student>> getStudentsPage(int pageNumber, int size) {
        Page<Student> resultPage = studentRepository.findAll(PageRequest.of(pageNumber, size));
        if (pageNumber > resultPage.getTotalPages()) {
            throw new PageOutOfBoundsException(pageNumber);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-pages", String.valueOf(resultPage.getTotalPages()));
        return new ResponseEntity<>(resultPage.getContent(), responseHeaders, HttpStatus.OK);
    }
}
