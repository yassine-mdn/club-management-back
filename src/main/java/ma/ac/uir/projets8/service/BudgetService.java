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
        budget.setBudget_restant(request.budget_initial());
        budget.setClub(clubRepository.findById(request.idClub()).orElseThrow(()->new ClubNotFoundException(request.idClub())));

        return budgetRepository.save(budget);
    }
}
