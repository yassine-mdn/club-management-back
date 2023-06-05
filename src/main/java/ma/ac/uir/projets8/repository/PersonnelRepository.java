package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonnelRepository extends JpaRepository<Personnel,Integer> {
    Optional<Personnel> findByEmail(String email);

    Boolean existsByEmail(String email);
}
