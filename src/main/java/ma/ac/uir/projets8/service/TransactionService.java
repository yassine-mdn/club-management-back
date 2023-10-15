package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.TransactionController;
import ma.ac.uir.projets8.exception.BudgetNotFoundException;
import ma.ac.uir.projets8.exception.TransactionNotFoundException;
import ma.ac.uir.projets8.model.Budget;
import ma.ac.uir.projets8.model.Transaction;
import ma.ac.uir.projets8.repository.BudgetRepository;
import ma.ac.uir.projets8.repository.EventRepository;
import ma.ac.uir.projets8.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final EventRepository eventRepository;


    public Transaction createTransaction(TransactionController.NewTransactionRequest request){
        Transaction transaction = new Transaction();
        transaction.setDate(request.date());
        transaction.setValeur(request.valeur());
        transaction.setEvent(eventRepository.findById(request.idEvent()).orElseThrow(() -> new TransactionNotFoundException(request.idEvent())));
        Budget budget = budgetRepository.findById(request.idBudget()).orElseThrow(()->new BudgetNotFoundException(request.idBudget()));
        budget.setUsed_budget(budget.getUsed_budget()+request.valeur());
        transaction.setBudget(budget);
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> getTransactionById(Long id){
        return transactionRepository.findById(id);

    }

    public List<Transaction> getAllTransaction(){
        return transactionRepository.findAll();
    }

    public Transaction updateTransaction(Transaction transaction){
        if(!transactionRepository.existsById(transaction.getIdTransaction())){
            throw new TransactionNotFoundException(transaction.getIdTransaction());
        }
        // update usedBudget attribute when a transaction is updated
        Budget budget = transaction.getBudget();
        budget.setUsed_budget(budget.getUsed_budget()+transaction.getValeur());

        Transaction oldTransaction = transactionRepository.findById(transaction.getIdTransaction()).orElseThrow(()->new TransactionNotFoundException(transaction.getIdTransaction()));
        Budget oldBudget = oldTransaction.getBudget();
        oldBudget.setUsed_budget(oldBudget.getUsed_budget()-oldTransaction.getValeur());

        return transactionRepository.save(transaction);

    }

    public void deleteTransaction(Long idTransaction){
        if(!transactionRepository.existsById(idTransaction)    ){
            throw new TransactionNotFoundException(idTransaction);
        }
        transactionRepository.deleteById(idTransaction);
    }
}
