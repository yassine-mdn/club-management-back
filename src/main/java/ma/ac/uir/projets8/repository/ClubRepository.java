package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.model.enums.ClubStatus;
import ma.ac.uir.projets8.model.enums.ClubType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClubRepository extends JpaRepository<Club, Integer> {


    @Query("select c from Club c where c.type in :types and c.status in :status " +
            "and (lower(c.name) like lower(concat('%',:keyword,'%')) or lower(c.description) like lower(concat('%',:keyword,'%') ) )")
    Page<Club> findAllByFiltered(
            @Param("types") List<ClubType> clubTypes,
            @Param("status") List<ClubStatus> statusList,
            @Param("keyword") String keyword,
            Pageable pageable);

    Page<Club> findAllByFeatured(Boolean featured, Pageable pageable);

    Page<Club> findAllByStatus(ClubStatus status, Pageable pageable);

    Page<Club> findAllByStatusIn(List<ClubStatus> statusList, Pageable pageable);
}
