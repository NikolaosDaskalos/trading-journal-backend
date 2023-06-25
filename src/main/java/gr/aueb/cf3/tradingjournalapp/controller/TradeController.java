package gr.aueb.cf3.tradingjournalapp.controller;

import gr.aueb.cf3.tradingjournalapp.dto.TradeDTO;
import gr.aueb.cf3.tradingjournalapp.model.Trade;
import gr.aueb.cf3.tradingjournalapp.service.ITradeService;
import gr.aueb.cf3.tradingjournalapp.validator.TradeValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Find trade by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trade found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TradeDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Trade Not found",
                    content = @Content)})
    @GetMapping("/trades/{tradeId}")
    @SneakyThrows
    public ResponseEntity<TradeDTO> getTradeById(@PathVariable("tradeId") Long tradeId, Principal principal) {
        Trade trade = tradeService.findTradeById(tradeId, principal.getName());
        return ResponseEntity.ok(mapToTradeDTO(trade));
    }

    @Operation(summary = "Find trades by ticker")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trades found",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TradeDTO.class)))})})
    @GetMapping("/trades/search/{ticker}")
    public ResponseEntity<List<TradeDTO>> getTradeByTicker(@PathVariable("ticker") String ticker, Principal principal) {
        List<Trade> trades = tradeService.findTradesByTicker(ticker, principal.getName());
        List<TradeDTO> tradeDTOs = trades.stream()
                .map(this::mapToTradeDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tradeDTOs);
    }

    @Operation(summary = "Find all user trades")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trades found",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TradeDTO.class)))})})
    @GetMapping("/trades")
    public ResponseEntity<List<TradeDTO>> getAllTrades(Principal principal) {
        List<Trade> trades = tradeService.findAllTradesByUser(principal.getName());
        List<TradeDTO> tradeDTOs = trades.stream()
                .map(this::mapToTradeDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tradeDTOs);
    }

    @Operation(summary = "Add new trade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trade added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TradeDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Trade missing fields",
                    content = @Content)})
    @PostMapping("/trades")
    @SneakyThrows
    public ResponseEntity<TradeDTO> addTrade(@RequestBody @Valid TradeDTO dto, Principal principal) {
        Trade trade = tradeService.createTrade(dto, principal.getName());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(trade.getId()).toUri();
        return ResponseEntity.created(location).body(mapToTradeDTO(trade));

    }

    @Operation(summary = "Update trade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trade updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TradeDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Trade Not found for this user",
                    content = @Content)})
    @PutMapping("/trades/{tradeId}")
    @SneakyThrows
    public ResponseEntity<TradeDTO> updateTrade(@PathVariable("tradeId") Long tradeId, @RequestBody @Valid TradeDTO dto, Principal principal, BindingResult bindingResult) {
        dto.setId(tradeId);
        Trade trade = tradeService.updateTrade(dto, principal.getName());
        return ResponseEntity.ok(mapToTradeDTO(trade));
    }

    @Operation(summary = "Delete trade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trade deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TradeDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Trade Not found",
                    content = @Content)})
    @DeleteMapping("/trades/{tradeId}")
    @SneakyThrows
    public ResponseEntity<TradeDTO> deleteTrade(@PathVariable("tradeId") Long tradeId, Principal principal) {
        Trade deletedTrade = tradeService.deleteTrade(tradeId, principal.getName());
        return ResponseEntity.ok(mapToTradeDTO(deletedTrade));
    }

    private TradeDTO mapToTradeDTO(Trade trade) {
        return TradeDTO.builder()
                .id(trade.getId())
                .ticker(trade.getTicker())
                .buyDate(trade.getBuyDate())
                .buyQuantity(trade.getBuyQuantity())
                .buyPrice(trade.getBuyPrice())
                .position(trade.getPosition().toString())
                .sellDate(trade.getSellDate())
                .sellQuantity(trade.getSellQuantity())
                .sellPrice(trade.getSellPrice())
                .profitLoss(trade.getProfitLoss())
                .build();
    }


    @InitBinder(value = "tradeDTO")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(tradeValidator);
    }

}
