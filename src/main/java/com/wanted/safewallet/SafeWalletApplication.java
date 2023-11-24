package com.wanted.safewallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class SafeWalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(SafeWalletApplication.class, args);
	}

}
