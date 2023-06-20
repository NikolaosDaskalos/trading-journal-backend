package gr.aueb.cf3.tradingjournalapp.service.exceptions;

public class EmailAlreadyExistsException extends Exception {
    private static final long serialVersionUID = 1L;

    public EmailAlreadyExistsException(String email) {
        super("User with email '" + email + "' already exist");
    }
}
