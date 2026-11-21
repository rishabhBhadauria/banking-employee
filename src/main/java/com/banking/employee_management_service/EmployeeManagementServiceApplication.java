package com.banking.employee_management_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@SpringBootApplication
public class EmployeeManagementServiceApplication {
	private static final Logger log = LoggerFactory.getLogger(EmployeeManagementServiceApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(EmployeeManagementServiceApplication.class, args);
		log.info("Hello! {} application is started" , EmployeeManagementServiceApplication.class.getSimpleName());
		System.out.println("Hello! Application is starting...");
	}

}
