package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Club;
import ma.ac.uir.projets8.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    Page<Student> findAllByJoinedClubs(Club c, Pageable pageable);
    Boolean existsByEmail(String email);

    @Query("""
        select s from Student s
        where (lower(s.firstName) like lower(concat('%',:keyword,'%')))
        or (lower(s.lastName) like lower(concat('%',:keyword,'%')))
        or (cast(s.studentId as string) like concat('%',:keyword,'%'))
        or (lower(s.email) like lower(concat('%',:keyword,'%')))
    """)
    Page<Student> findAllFiltered(@Param("keyword") String keyword, Pageable pageable);
}
