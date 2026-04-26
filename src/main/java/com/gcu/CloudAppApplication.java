package com.gcu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * CloudAppApplication is the main entry point for the Spring Boot application.
 * It is annotated with @SpringBootApplication, which is a convenience annotation that adds @Configuration, @EnableAutoConfiguration, and @ComponentScan.
 */
@SpringBootApplication
@ComponentScan({ "com.gcu" })
public class CloudAppApplication {

	/**
	 * The main method is the entry point of the application. It uses SpringApplication.run() to launch the application, passing in the CloudAppApplication class and any command-line arguments.
	 * @param args command-line arguments passed to the application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(CloudAppApplication.class, args);
	}

}
