package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.StudentController;
import ma.ac.uir.projets8.exception.CompteNotFoundException;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static ma.ac.uir.projets8.model.enums.Role.ADMIN;

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
        student.setRoles(List.of(ADMIN));
        studentRepository.save(student);
    }

    public Student getStudentById(Integer id){
        return studentRepository.findById(id).orElseThrow(() -> new CompteNotFoundException(id));
    }

    public void updateStudent(Integer id, StudentController.NewStudentRequest request) {
        studentRepository.findById(id)
                .map(student -> {
                            student.setFirstName(request.firstName());
                            student.setLastName(request.lastName());
                            student.setEmail(request.email());
                            student.setPassword(request.password());
                            return studentRepository.save(student);
                        }
                );
        //TODO:Add case of recieving an invalid id
    }

    public void deleteStudent(Integer id){

        studentRepository.deleteById(id);
    }
}
