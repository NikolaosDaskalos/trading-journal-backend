package gr.aueb.cf3.tradingjournalapp.validator;

import gr.aueb.cf3.tradingjournalapp.dto.TradeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@Slf4j
public class TradeValidator implements Validator {
    public boolean supports(Class<?> clazz) {
        return TradeDTO.class.equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        TradeDTO dto = (TradeDTO) target;

        if (dto.getSellDate() != null && dto.getSellDate().isBefore(dto.getBuyDate())) {
            log.error("wrong dates");
            errors.rejectValue("sellDate", "DateErr", "sell date cannot be earlier than buy date");
        }

        if (dto.getSellQuantity() != null && dto.getSellQuantity() > dto.getBuyQuantity()) {
            log.error("wrong quantities");
            errors.rejectValue("sellQuantity", "SellQuantityErr", "sell quantity must not exceed buy quantity");
        }
    }
}
