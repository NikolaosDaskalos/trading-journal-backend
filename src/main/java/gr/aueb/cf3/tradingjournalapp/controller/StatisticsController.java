package gr.aueb.cf3.tradingjournalapp.controller;

import gr.aueb.cf3.tradingjournalapp.dto.StatsDTO;
import gr.aueb.cf3.tradingjournalapp.model.Statistics;
import gr.aueb.cf3.tradingjournalapp.service.IStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StatisticsController {

    private final IStatisticsService statsService;

    @Operation(summary = "User statistics based on trades")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics calculated correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatsDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Internal error",
                    content = @Content)})
    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStatistics(Principal principal) {
        Statistics stats = statsService.calculateUserStats(principal.getName());
        return ResponseEntity.ok(mapToDTO(stats));
    }

    private StatsDTO mapToDTO(Statistics statistics) {
        return StatsDTO.builder()
                .id(statistics.getId())
                .profit(statistics.getProfit())
                .winRate(statistics.getWinRate())
                .openPositions(statistics.getOpenPositions())
                .gainsPerDay(statistics.getGainsPerDay())
                .build();
    }
}
