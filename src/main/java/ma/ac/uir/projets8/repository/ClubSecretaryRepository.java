package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Admin;
import ma.ac.uir.projets8.model.ClubSecretary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubSecretaryRepository extends JpaRepository<ClubSecretary, Integer> {
    Optional<Admin> findByEmail(String email);
}
