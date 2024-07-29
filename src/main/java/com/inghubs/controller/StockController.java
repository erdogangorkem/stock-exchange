package com.inghubs.controller;

import com.inghubs.dto.StockCreateDTO;
import com.inghubs.dto.StockDTO;
import com.inghubs.dto.StockPriceUpdateDTO;
import com.inghubs.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@Tag(name = "Stock-Controller", description = "Operations related to stocks")
public class StockController {
    private final StockService stockService;

    @Operation(summary = "Create a new stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Stock created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "409", description = "Stock already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<StockDTO> createStock(@Valid @RequestBody StockCreateDTO stockCreateDTO) {
        return new ResponseEntity<>(stockService.createStock(stockCreateDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update the price of an existing stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock price updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Stock not found", content = @Content)
    })
    @PutMapping
    public ResponseEntity<StockDTO> updateStockPrice(
            @Valid @RequestBody StockPriceUpdateDTO stockPriceUpdateDTO) {
        return new ResponseEntity<>(stockService.updateStockPrice(stockPriceUpdateDTO), HttpStatus.OK);
    }

    @Operation(summary = "Delete a stock by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Stock deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Stock not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(
            @Parameter(description = "ID of the stock to delete", required = true) @PathVariable Long id) {
        stockService.deleteStock(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
