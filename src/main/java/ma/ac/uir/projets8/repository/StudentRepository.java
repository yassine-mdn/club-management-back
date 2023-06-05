package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Personnel;
import ma.ac.uir.projets8.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Personnel> findByEmail(String email);

    Boolean existsByEmail(String email);
}
