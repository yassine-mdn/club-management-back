package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Admin;
import ma.ac.uir.projets8.model.ClubTreasurer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubTreasurerRepository extends JpaRepository<ClubTreasurer, Integer> {
    Optional<Admin> findByEmail(String email);
}
