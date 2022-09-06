package com.retto.testing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class TestingApplicationTests {

	@Test
	void contextLoads() {
		log.info("contextLoads");
	}
}
