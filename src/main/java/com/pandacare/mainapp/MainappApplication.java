package com.pandacare.mainapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MainappApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainappApplication.class, args);
	}

}
