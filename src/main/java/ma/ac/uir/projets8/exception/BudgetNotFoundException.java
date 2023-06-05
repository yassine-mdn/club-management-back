package ma.ac.uir.projets8.exception;

public class BudgetNotFoundException extends RuntimeException {

    public BudgetNotFoundException(Long id) {
        super("Budget not found with id: " + id);
    }
}

