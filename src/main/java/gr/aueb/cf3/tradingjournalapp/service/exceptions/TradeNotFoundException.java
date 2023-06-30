package gr.aueb.cf3.tradingjournalapp.service.exceptions;

public class TradeNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public TradeNotFoundException(Long id) {
        super("Trade with id: " + id + " did not exist");
    }
}
