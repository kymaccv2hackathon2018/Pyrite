package com.pyrite.cartabandonmentservice;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CartAbandonmentServiceApplication
{
	@PostConstruct
	public void init()
	{
		// Setting Spring Boot SetTimeZone
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args)
	{
		SpringApplication.run(CartAbandonmentServiceApplication.class, args);
	}
}
