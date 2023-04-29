package ma.ac.uir.projets8.exception;

public class PageOutOfBoundsException extends RuntimeException{

    public PageOutOfBoundsException(Integer page) {
        super("could not find page number : " + page);
    }
}
