package ma.ac.uir.projets8.exception;

public class MeetingNotFoundException extends RuntimeException{

    public MeetingNotFoundException(Integer id) {
        super("could not find meeting with id : " + id);
    }
}
