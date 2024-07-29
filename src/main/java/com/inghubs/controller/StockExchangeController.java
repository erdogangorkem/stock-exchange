package com.inghubs.controller;

import com.inghubs.dto.StockExchangeDTO;
import com.inghubs.service.StockExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stock-exchange")
@RequiredArgsConstructor
@Tag(name = "Stock-Exchange-Controller", description = "API for managing stock exchanges")
public class StockExchangeController {

    private final StockExchangeService stockExchangeService;

    @Operation(summary = "Retrieve a StockExchange by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "StockExchange retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockExchangeDTO.class))}),
            @ApiResponse(responseCode = "404", description = "StockExchange not found", content = @Content)
    })
    @GetMapping("/{name}")
    public ResponseEntity<StockExchangeDTO> getStockExchange(
            @Parameter(description = "Name of the StockExchange", required = true) @PathVariable String name) {
        StockExchangeDTO stockExchangeDTO = stockExchangeService.getStockExchange(name);
        return new ResponseEntity<>(stockExchangeDTO, HttpStatus.OK);
    }

    @Operation(summary = "Add a Stock to a StockExchange")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock added to StockExchange successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockExchangeDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Stock or StockExchange not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Stock already exists in StockExchange", content = @Content)
    })
    @PostMapping("/{name}")
    public ResponseEntity<StockExchangeDTO> addStockToStockExchange(
            @Parameter(description = "Name of the StockExchange", required = true) @PathVariable String name,
            @Parameter(description = "ID of the Stock to add", required = true) @RequestParam Long stockId) {
        StockExchangeDTO stockExchangeDTO = stockExchangeService.addStockToStockExchange(name, stockId);
        return new ResponseEntity<>(stockExchangeDTO, HttpStatus.OK);
    }

    @Operation(summary = "Remove a Stock from a StockExchange")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock removed from StockExchange successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockExchangeDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Stock or StockExchange not found", content = @Content)
    })
    @DeleteMapping("/{name}")
    public ResponseEntity<StockExchangeDTO> removeStockFromStockExchange(
            @Parameter(description = "Name of the StockExchange", required = true) @PathVariable String name,
            @Parameter(description = "ID of the Stock to remove", required = true) @RequestParam Long stockId) {
        StockExchangeDTO stockExchangeDTO = stockExchangeService.removeStockFromStockExchange(name, stockId);
        return new ResponseEntity<>(stockExchangeDTO, HttpStatus.OK);
    }
}