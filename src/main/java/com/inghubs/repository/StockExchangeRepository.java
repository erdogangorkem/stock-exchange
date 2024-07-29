package com.inghubs.repository;

import com.inghubs.entity.StockExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockExchangeRepository extends JpaRepository<StockExchange, Long> {
    Optional<StockExchange> findByName(String name);
    @Query("SELECT se FROM StockExchange se JOIN se.stocks s WHERE s.id = :stockId")
    List<StockExchange> findAllByStockId(@Param("stockId") Long stockId);
}