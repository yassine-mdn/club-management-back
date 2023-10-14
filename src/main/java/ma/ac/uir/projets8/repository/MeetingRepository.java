package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingRepository extends JpaRepository<Meeting,Integer> {

    @Query("select m from Meeting m where m.organiser.idA = :idA or exists (select p from m.participants p where p.idA = :idA)")
    public Page<Meeting> findAllByOrganisedOrParticipated(@Param("idA") Integer idA, Pageable pageable);
}
