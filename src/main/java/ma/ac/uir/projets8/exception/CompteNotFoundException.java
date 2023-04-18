package ma.ac.uir.projets8.exception;

import java.util.List;

public class CompteNotFoundException extends RuntimeException{

    public CompteNotFoundException(Integer id) {
        super("could not find compte with id : " + id);
    }
}
