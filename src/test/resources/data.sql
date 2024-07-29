INSERT INTO stock_exchange (name, description, live_in_market, version) VALUES
                                                                            ('A', 'A Stock Exchange', FALSE, 0),
                                                                            ('B', 'B Stock Exchange', FALSE, 0),
                                                                            ('C', 'C Stock Exchange', FALSE, 0);

INSERT INTO stock (name, description, current_price, last_update, version) VALUES
                                                                               ('Tesla', 'Tesla Inc.', 100.00, CURRENT_TIMESTAMP, 0),
                                                                               ('Amazon', 'Amazon.com Inc.', 200.00, CURRENT_TIMESTAMP, 0),
                                                                               ('Facebook', 'Meta Platforms Inc.', 300.00, CURRENT_TIMESTAMP, 0),
                                                                               ('Netflix', 'Netflix Inc.', 4000.00, CURRENT_TIMESTAMP, 0),
                                                                               ('Nvidia', 'Nvidia Corporation', 5000.00, CURRENT_TIMESTAMP, 0),
                                                                               ('Apple', 'Apple Inc.', 6000.00, CURRENT_TIMESTAMP, 0),
                                                                               ('Microsoft', 'Microsoft Corporation', 700.00, CURRENT_TIMESTAMP, 0),
                                                                               ('Google', 'Alphabet Inc.', 800.00, CURRENT_TIMESTAMP, 0);
