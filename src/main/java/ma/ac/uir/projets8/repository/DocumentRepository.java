package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {


    public Boolean existsByPath(String path);

    Page<Document> findAllBySender_IdC(Integer idC, Pageable pageable);
}
