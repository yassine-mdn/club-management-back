package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Transaction;
import ma.ac.uir.projets8.model.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    public Page<Transaction> findAllByBudget_IdBudget(Long budgetId, Pageable pageable);

    Page<Transaction> findAllByStatus(TransactionStatus status, Pageable pageable);
}
