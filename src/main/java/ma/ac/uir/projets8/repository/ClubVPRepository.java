package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Admin;
import ma.ac.uir.projets8.model.ClubVP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubVPRepository extends JpaRepository<ClubVP, Integer> {
    Optional<Admin> findByEmail(String email);
}
