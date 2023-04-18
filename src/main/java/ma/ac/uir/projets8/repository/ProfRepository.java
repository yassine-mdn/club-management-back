package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Admin;
import ma.ac.uir.projets8.model.Prof;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfRepository extends JpaRepository<Prof, Integer> {
    Optional<Admin> findByEmail(String email);
}
