package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.model.Transaction;
import ma.ac.uir.projets8.model.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ma.ac.uir.projets8.model.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {


    @Query("select e from Event e where e.status in :statusList " +
            "and ((lower(e.name) like lower(concat('%',:keyword,'%'))) or (lower(e.description) like lower(concat('%',:keyword,'%')) ) )")
    Page<Event> findAllByFilter(
            @Param("statusList") List<EventStatus> statusList,
            @Param("keyword") String searchKeyword,
            Pageable pageable
    );




    @Query("select e from Event e where e.organisateur.idC = :idClub and e.status in :statusList " +
            "and ( e.name like  lower(concat('%',:keyword,'%')) or e.description like (lower(concat('%',:keyword,'%') ) ))")
    Page<Event> findAllByFilterByOrganisateurIdc(
            @Param("idClub") Integer idC,
            @Param("statusList") List<EventStatus> statusList,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
        select e.transcations from Event e where e.idEvent = :idEvent
            """)
    Page<Transaction> findAllTransactionsByEventId(@Param("idEvent") Long idE, Pageable pageable);


    @Query("""
        select e.participants from Event e where e.idEvent = :idEvent
            """)
    Page<Student> findAllParticipantsByEventId(@Param("idEvent") Long idE, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.organisateur.idC = :clubID AND e.status IN :statusList")
    int countEventsByStatusAndClubId(@Param("clubID") Integer clubID, @Param("statusList") List<EventStatus> statusList);



}
