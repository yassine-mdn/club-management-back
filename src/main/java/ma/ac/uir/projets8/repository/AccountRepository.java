package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    @Query("SELECT a FROM Account a WHERE a.email = :email")
    Optional<Account> findByEmail(@Param("email") String email);

    Boolean existsByEmail(String email);
}
