package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Admin;
import ma.ac.uir.projets8.model.ClubPresident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubPresidentRepository extends JpaRepository<ClubPresident, Integer> {
    Optional<Admin> findByEmail(String email);
}
