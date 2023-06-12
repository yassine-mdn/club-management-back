package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.model.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import ma.ac.uir.projets8.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByOrganisateurAndStatusIn(Club club, List<EventStatus> statusList);
}
