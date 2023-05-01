package ma.ac.uir.projets8.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ma.ac.uir.projets8.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
