package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
