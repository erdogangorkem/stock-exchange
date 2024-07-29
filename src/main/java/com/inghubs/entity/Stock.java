package com.inghubs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String description;
    private BigDecimal currentPrice;
    private Timestamp lastUpdate;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "stocks")
    private Set<StockExchange> stockExchanges = new HashSet<>();

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.lastUpdate = Timestamp.from(Instant.now());
    }

    @Version
    private int version;

    @PreRemove
    public void removeFromStockExchange() {
        stockExchanges
                .forEach(se -> se.removeStock(this));
    }

}
