package gr.aueb.cf3.tradingjournalapp.service.exceptions;

public class UsernameAlreadyExistsException extends Exception {
    private static final long serialVersionUID = 1L;

    public UsernameAlreadyExistsException(String username) {
        super("User with Username: " + username + " already exists");
    }
}
