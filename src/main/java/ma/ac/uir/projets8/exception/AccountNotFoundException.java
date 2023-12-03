package ma.ac.uir.projets8.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Account not found")
public class AccountNotFoundException extends RuntimeException{

    public AccountNotFoundException() {
        super("could not find account");
    }

    public AccountNotFoundException(Integer id) {
        super("could not find account with id : " + id);
    }
}
