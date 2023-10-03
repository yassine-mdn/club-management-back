package ma.ac.uir.projets8.repository;

public interface EmailRepository {

    void sendSimpleEmail(String to, String subject, String content);
}
