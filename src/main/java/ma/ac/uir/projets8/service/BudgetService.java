package ma.ac.uir.projets8.service;


import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.exception.BudgetNotFoundException;
import ma.ac.uir.projets8.exception.DocumentNotFoundException;
import ma.ac.uir.projets8.model.Budget;
import ma.ac.uir.projets8.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;


    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public Budget updateBudget(Budget budget) {
        if (!budgetRepository.existsById(budget.getIdBudget())) {
            throw new BudgetNotFoundException(budget.getIdBudget());
        }
        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new BudgetNotFoundException(id);
        }
        budgetRepository.deleteById(id);
    }
}
