package ma.ac.uir.projets8.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.model.Budget;
import ma.ac.uir.projets8.model.Document;
import ma.ac.uir.projets8.model.Event;
import ma.ac.uir.projets8.model.Transaction;
import ma.ac.uir.projets8.model.enums.BudgetType;
import ma.ac.uir.projets8.service.BudgetService;
import ma.ac.uir.projets8.service.DocumentService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(summary = "Create a new Budget")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Budget created successfully"),
            @ApiResponse(responseCode = "400",description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<Budget> createBudget(@RequestBody BudgetController.NewBudgetRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.createBudget(request));
    }

    @Operation(summary = "Update budget by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Budget not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody BudgetController.NewBudgetRequest request){
        return ResponseEntity.ok(budgetService.updateBudgetById(id,request));
    }

    @Operation(summary = "Get Budget by id ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Budget successfully fetched"),
            @ApiResponse(responseCode = "404",description = "Budget not found"),
            @ApiResponse(responseCode = "400",description = "Invalid request")
    })
    @GetMapping("/{budget_id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable("budget_id") Long id){
        return ResponseEntity.ok(budgetService.findBudgetById(id));
    }

    @Operation(summary = "Delete Budget by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Budget deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Budget not found - the id is invalid")
    })
    @DeleteMapping("/{id_budget}")
    public ResponseEntity<String> deleteBudget(@PathVariable("id_budget") Long id_budget){
        return budgetService.deleteBudgetById(id_budget);
    }

    @Operation(summary = "get the list of transactions", description = "returns the associated list of transactions according to the budget with the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - the id is invalid", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    @GetMapping("{budget_id}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionsByBudget(@PathVariable("budget_id") Long id){
        return budgetService.getTransactionsByBudget(id);
    }

    public record NewBudgetRequest(
            BudgetType budgetType,
            double budget_initial,
            int idClub

    ) {
    }

}

