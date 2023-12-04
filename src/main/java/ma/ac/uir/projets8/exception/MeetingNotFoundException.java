package ma.ac.uir.projets8.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "meeting not found")
public class MeetingNotFoundException extends RuntimeException{

    public MeetingNotFoundException(Integer id) {
        super("could not find meeting with id : " + id);
    }
}
