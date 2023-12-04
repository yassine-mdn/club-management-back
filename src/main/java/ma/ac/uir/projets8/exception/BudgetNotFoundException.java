package ma.ac.uir.projets8.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND , reason = "Budget not found!")
public class BudgetNotFoundException extends RuntimeException {

    public BudgetNotFoundException(Long id) {
        super("Budget not found with id: " + id);
    }
}

