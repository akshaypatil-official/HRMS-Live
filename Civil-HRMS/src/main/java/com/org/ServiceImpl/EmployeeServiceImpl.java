package com.org.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.org.Entity.User;
import com.org.Repository.EmployeeRepository;
import com.org.Service.EmployeeService;


@Service
public class EmployeeServiceImpl implements EmployeeService {

	private final EmployeeRepository empRepository;

    EmployeeServiceImpl(EmployeeRepository empRepository) {
        this.empRepository = empRepository;
    }
	
	@Override
    public List<User> getEmployeesByKeyword(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return empRepository.findByFirstNameContainingIgnoreCase(keyword);
        }
        return empRepository.findAll();
    }


	@Override
    public void deleteEmployee(Long user_id) {
        empRepository.executeDelete(user_id);
    }
	
	@Override
	public User getUserById(Long user_id) {
	    return empRepository.findById(user_id)
	        .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + user_id));
	}

	@Override
	public void updateEmployee(Long user_id, User user) {
	    // 1. Fetch the existing database entity
	    User existingUser = empRepository.findById(user_id)
	        .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + user_id));
	    
	    // 2. Map the updated fields from the form submission
	    existingUser.setFirstName(user.getFirstName());
	    existingUser.setLastName(user.getLastName());
	    existingUser.setEmail(user.getEmail());
	    existingUser.setPhoneNo(user.getPhoneNo());
	    existingUser.setDesignation(user.getDesignation());
	    existingUser.setDob(user.getDob());
	    existingUser.setDoj(user.getDoj());
	    existingUser.setAadharNO(user.getAadharNO());
	    existingUser.setAddress(user.getAddress());
	    existingUser.setPanCardNo(user.getPanCardNo());
	    existingUser.setGender(user.getGender());
	 
	    empRepository.save(existingUser);
	}
}
