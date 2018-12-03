package com.pyrite.cartabandonmentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CartAbandonmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartAbandonmentServiceApplication.class, args);
	}
}
