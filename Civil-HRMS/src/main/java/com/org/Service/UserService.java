package com.org.Service;


import java.util.List;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.org.DTO.UserRegistrationDto;
import com.org.Entity.User;


public interface UserService extends UserDetailsService{
    
	User save(UserRegistrationDto registrationDto);
	
	User getUserByEmail(String email);

//	Timesheet name 
	
	String getFirstName();
    String getLastName();

	List<String> getAllActiveUsernames();

	String getCompanyName(Long user_Id);

    
}
