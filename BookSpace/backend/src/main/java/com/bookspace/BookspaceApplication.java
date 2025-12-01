package com.bookspace;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class BookspaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookspaceApplication.class, args);
	}

}
