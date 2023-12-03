package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.ClubDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClubDetailsRepository extends JpaRepository<ClubDetails, Integer> {

    @Query("""
            select c from ClubDetails c where (lower(c.club.name) like lower(concat('%',:keyword,'%')))
             or (lower(c.club.description) like lower(concat('%',:keyword,'%')))
             or (lower(c.description) like lower(concat('%',:keyword,'%')))
             or (lower(c.aboutUs) like lower(concat('%',:keyword,'%')))
            """)
    Page<ClubDetails> findAllFiltered(@Param(value = "keyword") String keyword, Pageable pageable);
}
