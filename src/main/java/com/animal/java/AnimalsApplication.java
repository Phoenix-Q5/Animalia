package com.animal.java;

import com.animal.java.config.OpenAPIConfiguration;
import com.animal.java.datasource.CommonDataSourceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAutoConfiguration(exclude = {
		DataSourceAutoConfiguration.class,
		JooqAutoConfiguration.class
})
@EnableAsync
@ComponentScan(basePackageClasses = AnimalsApplication.class)
@Import({OpenAPIConfiguration.class,
		CommonDataSourceConfiguration.class})
public class AnimalsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnimalsApplication.class, args);
	}

}
