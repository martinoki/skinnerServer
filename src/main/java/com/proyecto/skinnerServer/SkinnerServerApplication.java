package com.proyecto.skinnerServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SkinnerServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkinnerServerApplication.class, args);
	}

}
