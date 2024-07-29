package com.inghubs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class StockExchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockExchangeApplication.class, args);
	}

}
