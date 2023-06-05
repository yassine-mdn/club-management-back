package ma.ac.uir.projets8.exception;

public class ClubNotFoundException extends RuntimeException {

    public ClubNotFoundException(Integer id) {
        super("could not find club with id : " + id);
    }

}
