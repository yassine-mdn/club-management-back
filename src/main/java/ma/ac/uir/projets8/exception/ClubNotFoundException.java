package ma.ac.uir.projets8.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "club not found")
public class ClubNotFoundException extends RuntimeException {

    public ClubNotFoundException(Integer id) {
        super("could not find club with id : " + id);
    }

}
