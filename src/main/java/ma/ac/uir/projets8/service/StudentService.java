package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.StudentController;
import ma.ac.uir.projets8.exception.AccountNotFoundException;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.model.enums.Role;
import ma.ac.uir.projets8.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static ma.ac.uir.projets8.model.enums.Role.STUDENT;

@RequiredArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void addStudent(@RequestBody StudentController.NewStudentRequest request) {
        Student student = new Student();
        student.setLastName(request.lastName());
        student.setFirstName(request.firstName());
        student.setEmail(request.email());
        student.setPassword(request.password());
        if (request.role().isEmpty())
            student.setRoles(List.of(STUDENT));
        else
            student.setRoles(List.of(STUDENT, Role.valueOf(request.role())));
        studentRepository.save(student);
    }

    public Student getStudentById(Integer id) {
        return studentRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    public void updateStudent(Integer id, StudentController.NewStudentRequest request) {
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
                            if (!request.role().isEmpty())
                                student.setRoles(List.of(STUDENT, Role.valueOf(request.role())));
                            return studentRepository.save(student);
                        }
                );
        //TODO:Add case of recieving an invalid id
    }

    public void deleteStudent(Integer id) {

        studentRepository.deleteById(id);
    }
}
