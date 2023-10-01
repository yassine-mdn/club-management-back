package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.model.enums.ClubStatus;
import ma.ac.uir.projets8.model.enums.ClubType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Integer> {


    Page<Club> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);
    List<Club> findAllByStatus(ClubStatus status);

    List<Club> findAllByStatusIn(List<ClubStatus> statusList);
}
