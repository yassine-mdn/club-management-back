package ma.ac.uir.projets8.service;


import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.BudgetController;
import ma.ac.uir.projets8.controller.TransactionController;
import ma.ac.uir.projets8.exception.*;
import ma.ac.uir.projets8.model.Budget;
import ma.ac.uir.projets8.model.Transaction;
import ma.ac.uir.projets8.repository.BudgetRepository;
import ma.ac.uir.projets8.repository.ClubRepository;
import ma.ac.uir.projets8.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final TransactionRepository transactionRepository;

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

    // remise a zero du budget utilise par chaque club chaque annee (le 1er septembre)
    @Scheduled(cron = "0 0 0 1 9 ?")
    public void updateUsedBudgetOnceAYear(){
        budgetRepository.findAll().forEach(budget -> budget.setUsed_budget(0));
    }

    public Page<Transaction> getTransactionsByBudget(Long id, Integer pageNumber, Integer pageSize){

        if (pageNumber < 0 || pageSize < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid request");
        }

        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by("date").descending());
        Page<Transaction> transactionsPage = transactionRepository.findAllByBudget_IdBudget(id, pageable);
        
        if (transactionsPage.isEmpty()) throw new BudgetNotFoundException(id);

        if (pageNumber > transactionsPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }

        return transactionsPage;

    }
}
