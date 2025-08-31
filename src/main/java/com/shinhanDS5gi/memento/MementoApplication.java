package com.shinhanDS5gi.memento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(modifyOnCreate = false)
@SpringBootApplication
public class MementoApplication {


    public static void main(String[] args) {
		SpringApplication.run(MementoApplication.class, args);
	}

}
