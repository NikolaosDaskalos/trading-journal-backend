package gr.aueb.cf3.tradingjournalapp.service.exceptions;

public class TradeNotFoundException extends Throwable {
    private static final long serialVersionUID = 1L;

    public TradeNotFoundException(Long id) {
        super("Trade with id: " + id + " did not exist");
    }

    public TradeNotFoundException(String ticker) {
        super("Trades with ticker: " + ticker + " did not exist");
    }
}
