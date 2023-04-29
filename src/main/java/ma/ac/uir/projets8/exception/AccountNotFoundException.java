package ma.ac.uir.projets8.exception;

public class AccountNotFoundException extends RuntimeException{

    public AccountNotFoundException(Integer id) {
        super("could not find compte with id : " + id);
    }
}
