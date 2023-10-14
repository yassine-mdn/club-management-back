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


}
