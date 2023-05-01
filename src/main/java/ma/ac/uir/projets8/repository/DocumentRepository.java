package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
