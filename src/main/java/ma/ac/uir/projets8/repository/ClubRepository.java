package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.model.enums.ClubStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Integer> {

    List<Club> findAllByStatus(ClubStatus status);
}
