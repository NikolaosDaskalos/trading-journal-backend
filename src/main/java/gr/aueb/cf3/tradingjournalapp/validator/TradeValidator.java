package gr.aueb.cf3.tradingjournalapp.validator;

import gr.aueb.cf3.tradingjournalapp.dto.TradeDTO;
import gr.aueb.cf3.tradingjournalapp.model.Trade;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class TradeValidator implements Validator {
    public boolean supports(Class<?> clazz) {
        return Trade.class.equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        TradeDTO dto = (TradeDTO) target;
        if (dto.getSellDate().isBefore(dto.getBuyDate())) {
            errors.rejectValue("sellDate", "earlier than buyDate");
        }

        if (dto.getSellQuantity() > dto.getBuyQuantity()) {
            errors.rejectValue("sellQuantity", "exceeds buyQuantity");
        }
    }
}
