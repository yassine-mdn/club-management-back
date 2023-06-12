package ma.ac.uir.projets8.service;


import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.exception.BudgetNotFoundException;
import ma.ac.uir.projets8.exception.DocumentNotFoundException;
import ma.ac.uir.projets8.exception.MeetingNotFoundException;
import ma.ac.uir.projets8.model.Budget;
import ma.ac.uir.projets8.model.Transaction;
import ma.ac.uir.projets8.repository.BudgetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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

    public Budget updateBudget(Long id, Budget budget) {
        return budgetRepository.findById(id)
                .map(updatedBudget -> {
                    updatedBudget.setBudget_initial(budget.getBudget_initial());
                    updatedBudget.setBudget_restant(budget.getBudget_restant());
                    return budgetRepository.save(budget);
                })
                .orElseThrow(() -> new BudgetNotFoundException(id));
    }

    public void deleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new BudgetNotFoundException(id);
        }
        budgetRepository.deleteById(id);
    }

    public ResponseEntity<List<Transaction>> getTransactionsByBudget(Long id){
        try {
            return ResponseEntity.ok(new ArrayList<>(budgetRepository.findById(id).orElseThrow(() -> new BudgetNotFoundException(id)).getTransactions()));
        }catch (MeetingNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
