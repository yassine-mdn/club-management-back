package ma.ac.uir.projets8.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.model.Transaction;
import ma.ac.uir.projets8.model.enums.TransactionStatus;
import ma.ac.uir.projets8.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Request a new transaction", description = "Request a new transaction with a pending status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction successfully requested"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody NewTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(request));
    }

    @Operation(summary = "Get Page of transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    headers={@Header(name="total-pages", description = "the total number of pages", schema = @Schema(type = "string"))})
    })
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "25") Integer pageSize
    ) {

        Page<Transaction> transactionPage = transactionService.getAllTransaction(pageNumber, pageSize);
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.set("total-pages",String.valueOf(transactionPage.getTotalPages()));

        return new ResponseEntity<>(transactionPage.getContent(),responseHeader,HttpStatus.OK);
    }

    @Operation(summary = "Get a transaction by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Optional<Transaction> transactionOptional = transactionService.getTransactionById(id);
        if (transactionOptional.isPresent()) {
            return ResponseEntity.ok(transactionOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Deprecated
    @Operation(summary = "Update a transaction", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction successfully updated"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction updatedTransaction) {
        return ResponseEntity.ok(transactionService.updateTransaction(updatedTransaction));
    }

    @Operation(summary = "Delete a transaction by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transaction successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }


    public record NewTransactionRequest(
        Date date,
        double valeur,
        Long idBudget,
        Long idEvent
    ) {
    }
}

