package gr.aueb.cf3.tradingjournalapp.controller

import gr.aueb.cf3.tradingjournalapp.TestSpec
import gr.aueb.cf3.tradingjournalapp.dto.TradeDTO
import gr.aueb.cf3.tradingjournalapp.model.Position
import gr.aueb.cf3.tradingjournalapp.model.Trade
import gr.aueb.cf3.tradingjournalapp.model.User
import gr.aueb.cf3.tradingjournalapp.service.ITradeService
import gr.aueb.cf3.tradingjournalapp.service.TradeServiceImpl
import gr.aueb.cf3.tradingjournalapp.validator.TradeValidator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import spock.lang.Ignore
import spock.lang.Subject

import java.security.Principal
import java.time.LocalDate

class TradeControllerTest extends TestSpec {

    TradeValidator tradeValidator = Mock(TradeValidator)
    ITradeService tradeService = Mock(TradeServiceImpl)
    Principal principal = Mock(Principal)
    BindingResult bindingResult = Mock(BindingResult)

    Trade trade = new Trade(1L, 'AUEB', LocalDate.now(), 2, new BigDecimal('23.89'),
            Position.SHORT, LocalDate.now(), 2, new BigDecimal('48.09'), BigDecimal.ZERO, new User(id: 1L, username: 'testUser'))

    TradeDTO tradeDTO = new TradeDTO(1L, 'AUEB', LocalDate.now(), 2, new BigDecimal('23.89'),
            'SHORT', LocalDate.now(), 2, new BigDecimal('48.09'), BigDecimal.ZERO)

    @Subject
    TradeController tradeController = new TradeController(tradeService, tradeValidator)

    def "Happy path - get Trade by Id "() {
        when: "getTradeById method is invoked"
            ResponseEntity response = tradeController.getTradeById(1L, principal)

        then: "tradeService is invoked"
            1 * principal.getName() >> 'testUser'
            1 * tradeService.findTradeById(1L, 'testUser') >> trade
            0 * _

        and: "response body and status are correct"
            response.body == tradeDTO
            response.statusCode == HttpStatus.OK
    }

    def "Happy path - get Trades by ticker "() {
        when: "getTradeByTicker method is invoked"
            ResponseEntity response = tradeController.getTradeByTicker('AUEB', principal)

        then: "tradeService is invoked"
            1 * principal.getName() >> 'testUser'
            1 * tradeService.findTradesByTicker('AUEB', 'testUser') >> [trade, trade]
            0 * _

        and: "response body and status are correct"
            response.body == [tradeDTO, tradeDTO]
            response.statusCode == HttpStatus.OK
    }

    def "Happy path - get all trades"() {
        when: "getAllTrades method is invoked"
            ResponseEntity response = tradeController.getAllTrades(principal)

        then: "tradeService is invoked"
            1 * principal.getName() >> 'testUser'
            1 * tradeService.findAllTradesByUser('testUser') >> [trade, trade, trade]
            0 * _

        and: "response body and status are correct"
            response.body == [tradeDTO, tradeDTO, tradeDTO]
            response.statusCode == HttpStatus.OK
    }

    @Ignore
    def "Happy path - add Trade"() {
        when: "addTrade method is invoked"
            ResponseEntity response = tradeController.addTrade(tradeDTO, principal)

        then: "tradeService is invoked"
            1 * principal.getName() >> 'testUser'
            1 * tradeService.createTrade(tradeDTO, 'testUser') >> trade
            0 * _

        and: "response body and status are correct"
            println response.headers.get('location')
            response.body == tradeDTO
            response.statusCode == HttpStatus.CREATED
    }


    def "Happy path - update Trade"() {
        when: "updateTrade method is invoked"
            ResponseEntity response = tradeController.updateTrade(1L, tradeDTO, principal, bindingResult)

        then: "tradeService is invoked"
            1 * tradeValidator.validate(tradeDTO, bindingResult)
            1 * bindingResult.hasErrors() >> false
            1 * principal.getName() >> 'testUser'
            1 * tradeService.updateTrade(tradeDTO, 'testUser') >> trade
            0 * _

        and: "response body and status are correct"
            response.body == tradeDTO
            response.statusCode == HttpStatus.OK
    }

    def "Happy path - delete Trade"() {
        when: "deleteTrade method is invoked"
            ResponseEntity response = tradeController.deleteTrade(1L, principal)

        then: "tradeService is invoked"
            1 * principal.getName() >> 'testUser'
            1 * tradeService.deleteTrade(1L, 'testUser') >> trade
            0 * _

        and: "response body and status are correct"
            response.body == tradeDTO
            response.statusCode == HttpStatus.OK
    }

}
