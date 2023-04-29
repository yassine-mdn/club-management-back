package ma.ac.uir.projets8.service;

import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.model.enums.Role;
import ma.ac.uir.projets8.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

class StudentServiceTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;

    private Student student1 = new Student();

    private Student student2 = new Student();


    @BeforeEach
    public void setUp() {
        student1.setEmail("sudent1@mail.com");
        student1.setFirstName("test");
        student1.setLastName("testStudent1");
        student1.setPassword("test");
        student1.setRoles(List.of(Role.STUDENT, Role.PRESIDENT));
        studentRepository.save(student1);

        student2.setEmail("sudent2@mail.com");
        student2.setFirstName("test");
        student2.setLastName("testStudent2");
        student2.setPassword("test");
        student2.setRoles(List.of(Role.STUDENT, Role.VICE_PRESIDENT));
        studentRepository.save(student2);

    }
    @Test
    void getStudentPage() {
        List<Student> students = studentService.getStudentsPage(0, 1).getBody();
        assertEquals(1, students.size());
    }
}