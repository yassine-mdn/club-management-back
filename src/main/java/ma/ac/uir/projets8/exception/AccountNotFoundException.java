package ma.ac.uir.projets8.exception;

public class AccountNotFoundException extends RuntimeException{

    public AccountNotFoundException() {
        super("could not find account");
    }

    public AccountNotFoundException(Integer id) {
        super("could not find account with id : " + id);
    }
}
