package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.exception.TransactionNotFoundException;
import ma.ac.uir.projets8.model.Transaction;
import ma.ac.uir.projets8.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;


    public Transaction createTransaction(Transaction transaction){
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
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long idTransaction){
        if(!transactionRepository.existsById(idTransaction)    ){
            throw new TransactionNotFoundException(idTransaction);
        }
        transactionRepository.deleteById(idTransaction);
    }
}
