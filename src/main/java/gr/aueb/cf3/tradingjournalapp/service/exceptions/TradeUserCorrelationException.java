package gr.aueb.cf3.tradingjournalapp.service.exceptions;

public class TradeUserCorrelationException extends Throwable {
    private static final long serialVersionUID = 1L;

    public TradeUserCorrelationException(Long tradeId, String username) {
        super(String.format("Trade with id %s does not belong to user %s", tradeId, username));
    }
}
