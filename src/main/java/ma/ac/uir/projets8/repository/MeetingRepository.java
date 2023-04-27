package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting,Integer> {
}
