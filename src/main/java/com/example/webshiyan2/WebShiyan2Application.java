package com.example.webshiyan2;

import com.example.webshiyan2.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class WebShiyan2Application {

	public static void main(String[] args) {
		SpringApplication.run(WebShiyan2Application.class, args);
	}

}
