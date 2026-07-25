package com.org;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.org.Entity.Company;
import com.org.Repository.CompanyRepository;

@SpringBootApplication
public class CivilHrmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CivilHrmsApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner initData(CompanyRepository companyRepo) {
	    return args -> {
	        // Fixed typo: Changed "Infraprojects" to "Imfraprojects" to match the constructor
	        if (!companyRepo.existsByName("Civil Deck Infraprojects Pvt Ltd")) {
	            companyRepo.save(new Company("Civil Deck Infraprojects Pvt Ltd"));
	        }
	        if (!companyRepo.existsByName("Gawali Engineering And Company")) {
	            companyRepo.save(new Company("Gawali Engineering And Company"));
	        }
	    };
	}


}
