package gr.aueb.cf3.tradingjournalapp.controller;

import gr.aueb.cf3.tradingjournalapp.dto.TradeDTO;
import gr.aueb.cf3.tradingjournalapp.model.Trade;
import gr.aueb.cf3.tradingjournalapp.service.ITradeService;
import gr.aueb.cf3.tradingjournalapp.validator.TradeValidator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class TradeController {
    private final ITradeService tradeService;
    private final TradeValidator tradeValidator;

    @GetMapping("/trades/{tradeId}")
    @SneakyThrows
    public ResponseEntity<TradeDTO> getTradeById(@PathVariable("tradeId") Long tradeId, Principal principal) {
        Trade trade = tradeService.findTradeById(tradeId, principal.getName());
        return ResponseEntity.ok(mapToTradeDTO(trade));
    }

    @GetMapping("/trades/search/{ticker}")
    public ResponseEntity<List<TradeDTO>> getTradeByTicker(@PathVariable("ticker") String ticker, Principal principal) {
        List<Trade> trades = tradeService.findTradesByTicker(ticker, principal.getName());
        List<TradeDTO> tradeDTOs = trades.stream()
                .map(this::mapToTradeDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tradeDTOs);
    }

    @GetMapping("/trades")
    public ResponseEntity<List<TradeDTO>> getAllTrades(Principal principal) {
        List<Trade> trades = tradeService.findAllTradesByUser(principal.getName());
        List<TradeDTO> tradeDTOs = trades.stream()
                .map(this::mapToTradeDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tradeDTOs);
    }

    @PostMapping("/trades")
    @SneakyThrows
    public ResponseEntity<TradeDTO> addTrade(@RequestBody @Valid TradeDTO dto, Principal principal) {
        Trade trade = tradeService.createTrade(dto, principal.getName());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(trade.getId()).toUri();
        return ResponseEntity.created(location).body(mapToTradeDTO(trade));

    }

    @PutMapping("/trades/{tradeId}")
    @SneakyThrows
    public ResponseEntity<TradeDTO> updateTrade(@PathVariable("tradeId") Long tradeId, @RequestBody @Valid TradeDTO dto, Principal principal, BindingResult bindingResult) {
//        tradeValidator.validate(dto, bindingResult);
        dto.setId(tradeId);
        Trade trade = tradeService.updateTrade(dto, principal.getName());
        return ResponseEntity.ok(mapToTradeDTO(trade));
    }

    @DeleteMapping("/trades/{tradeId}")
    @SneakyThrows
    public ResponseEntity<TradeDTO> deleteTrade(@PathVariable("tradeId") Long tradeId, Principal principal) {
        Trade deletedTrade = tradeService.deleteTrade(tradeId, principal.getName());
        return ResponseEntity.ok(mapToTradeDTO(deletedTrade));
    }

    private TradeDTO mapToTradeDTO(Trade trade) {
        return TradeDTO.builder()
                .id(trade.getId())
                .companyName(trade.getCompanyName())
                .ticker(trade.getTicker())
                .buyDate(trade.getBuyDate())
                .buyQuantity(trade.getBuyQuantity())
                .buyPrice(trade.getBuyPrice())
                .position(trade.getPosition().toString())
                .sellDate(trade.getSellDate())
                .sellQuantity(trade.getSellQuantity())
                .sellPrice(trade.getSellPrice())
                .build();
    }

    @InitBinder(value = "tradeDTO")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(tradeValidator);
    }

}
