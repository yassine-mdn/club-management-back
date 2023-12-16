package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.controller.TransactionController;
import ma.ac.uir.projets8.exception.BudgetNotFoundException;
import ma.ac.uir.projets8.exception.PageOutOfBoundsException;
import ma.ac.uir.projets8.exception.TransactionNotFoundException;
import ma.ac.uir.projets8.model.Budget;
import ma.ac.uir.projets8.model.Transaction;
import ma.ac.uir.projets8.model.enums.TransactionStatus;
import ma.ac.uir.projets8.repository.BudgetRepository;
import ma.ac.uir.projets8.repository.EventRepository;
import ma.ac.uir.projets8.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setEvent(eventRepository.findById(request.idEvent()).orElseThrow(() -> new TransactionNotFoundException(request.idEvent())));
        Budget budget = budgetRepository.findById(request.idBudget()).orElseThrow(()->new BudgetNotFoundException(request.idBudget()));

        transaction.setBudget(budget);
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> getTransactionById(Long id){
        return transactionRepository.findById(id);

    }

    public Page<Transaction> getAllTransaction(Integer pageSize,Integer pageNumber){
        if(pageNumber<0 || pageSize<0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"invalide pageSize or pageNumber in getAllTransactions");
        }
        Pageable pageable = PageRequest.of(pageSize,pageNumber, Sort.by("date").descending());
        Page<Transaction> transactionPage = transactionRepository.findAll(pageable);

        if(transactionPage.getTotalPages()<pageNumber){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }


        return transactionPage;
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
        // update usedBudget when an APPROVED transaction is deleted
        Transaction transaction = transactionRepository.findById(idTransaction).orElseThrow(()->new TransactionNotFoundException(idTransaction));
        if(transaction.getStatus().equals(TransactionStatus.APPROVED)){
            Budget budget = transaction.getBudget();
            budget.setUsed_budget(budget.getUsed_budget()-transaction.getValeur());
        }
        transactionRepository.deleteById(idTransaction);
    }

    public void approveTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
        if (transaction.getStatus().equals(TransactionStatus.APPROVED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "transaction already approved");
        }
        Budget budget = transaction.getBudget();
        if(budget.getUsed_budget()+transaction.getValeur()>budget.getBudget_initial()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "budget exceeded");
        }
        budget.setUsed_budget(budget.getUsed_budget() + transaction.getValeur());
        transaction.setStatus(TransactionStatus.APPROVED);
        transactionRepository.save(transaction);
    }

    public void rejectTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
        if (transaction.getStatus().equals(TransactionStatus.REJECTED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "transaction already rejected");
        }
        if (transaction.getStatus().equals(TransactionStatus.APPROVED)) {
            Budget budget = transaction.getBudget();
            budget.setUsed_budget(budget.getUsed_budget() - transaction.getValeur());
        }
        transaction.setStatus(TransactionStatus.REJECTED);
        transactionRepository.save(transaction);
    }

    public Page<Transaction> getAllTransactionByStatus(TransactionStatus status, Integer pageNumber, Integer pageSize) {
        if (pageNumber < 0 || pageSize < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalide pageSize or pageNumber in getAllTransactions");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("date").descending());
        Page<Transaction> transactionPage = transactionRepository.findAllByStatus(status, pageable);

        if (transactionPage.getTotalPages() < pageNumber) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, new PageOutOfBoundsException(pageNumber).getMessage());
        }
        return transactionPage;
    }
}
