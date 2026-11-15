package com.fandza.ccs.Fandza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FandzaApplication {

	public static void main(String[] args) {
		SpringApplication.run(FandzaApplication.class, args);
	}

}
