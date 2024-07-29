package com.inghubs.entity;

import com.inghubs.util.AppConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
public class StockExchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String description;
    private boolean liveInMarket;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "stock_exchange_stock",
            joinColumns = @JoinColumn(name = "stock_exchange_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "stock_id", referencedColumnName = "id")
    )
    private Set<Stock> stocks = new HashSet<>();

    @Version
    private int version;

    public void addStock(Stock stock) {
        this.stocks.add(stock);
        updateLiveInMarketStatus();
    }

    public void removeStock(Stock stock) {
        this.stocks.remove(stock);
        updateLiveInMarketStatus();
    }

    private void updateLiveInMarketStatus() {
        this.liveInMarket = this.stocks.size() >= AppConstants.STOCK_EXCHANGE_MINIMUM_LIVE_THRESHOLD;
    }
}
