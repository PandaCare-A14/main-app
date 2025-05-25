package com.pandacare.mainapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import com.pandacare.mainapp.config.TestSecurityConfig;

@SpringBootTest
@Import(TestSecurityConfig.class)
class MainappApplicationTests {

	@Test
	void contextLoads() {
	}

}
