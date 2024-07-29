package com.inghubs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class StockExchangeApplicationTests {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void contextLoads() {
		assertThat(context).isNotNull();
	}

	@Test
	void testDatabaseInitialization() {
		Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM stock", Integer.class);
		assertThat(count).isEqualTo(8);

		count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM stock_exchange", Integer.class);
		assertThat(count).isEqualTo(3);
	}
}
