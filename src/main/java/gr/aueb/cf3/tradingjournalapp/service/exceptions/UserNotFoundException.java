package gr.aueb.cf3.tradingjournalapp.service.exceptions;

public class UserNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String username) {
        super("User with Username: " + username + " not found");
    }

    public UserNotFoundException(Long id) {
        super("User with id: " + id + " not found");
    }
}
