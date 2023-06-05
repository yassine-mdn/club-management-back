package ma.ac.uir.projets8.exception;

public class ClubNotFoundException extends RuntimeException {

    public ClubNotFoundException(Long id) {
        super("Club not found with id: " + id);
    }
}
