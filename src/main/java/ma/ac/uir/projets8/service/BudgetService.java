package ma.ac.uir.projets8.service;


import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.BudgetController;
import ma.ac.uir.projets8.controller.TransactionController;
import ma.ac.uir.projets8.exception.*;
import ma.ac.uir.projets8.model.Budget;
import ma.ac.uir.projets8.model.Transaction;
import ma.ac.uir.projets8.repository.BudgetRepository;
import ma.ac.uir.projets8.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final BudgetRepository budgetRepository;
    private final ClubRepository clubRepository;

    public Budget createBudget(BudgetController.NewBudgetRequest request){
        Budget budget = new Budget();
        budget.setBudgetType(request.budgetType());
        budget.setBudget_initial(request.budget_initial());
        budget.setUsed_budget(0);
        budget.setClub(clubRepository.findById(request.idClub()).orElseThrow(()->new ClubNotFoundException(request.idClub())));

        return budgetRepository.save(budget);
    }

    public Budget updateBudgetById(Long id, BudgetController.NewBudgetRequest request){
        // i assume that budget_restant is automatically updated and shouldn't change by a request
        // the same goes for transactions
        Budget budget = budgetRepository.findById(id).orElseThrow(()-> new BudgetNotFoundException(id));
        budget.setBudget_initial(request.budget_initial());
        budget.setBudgetType(request.budgetType());
        budget.setClub(clubRepository.findById(request.idClub()).orElseThrow(()->new ClubNotFoundException(request.idClub())));
        return budgetRepository.save(budget);
    }

    public Budget findBudgetById(Long id){
        return budgetRepository.findById(id).orElseThrow(()-> new BudgetNotFoundException(id));
    }

    public ResponseEntity<String> deleteBudgetById(Long id){
        try{
            budgetRepository.deleteById(id);
            return new ResponseEntity<>("Budget deleted succefully", HttpStatus.OK);
        }catch (RuntimeException  exception){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,new BudgetNotFoundException(id).getMessage());
        }
    }
}
