package ma.ac.uir.projets8.repository;

import ma.ac.uir.projets8.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
}
