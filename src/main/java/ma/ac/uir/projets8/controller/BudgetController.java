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
    //todo: get budget by id
    //todo: delete budget
    //todo: schedule budget update once a year

    public record NewBudgetRequest(
            BudgetType budgetType,
            double budget_initial,

            int idClub

    ) {
    }

}

