package com.org.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.org.Entity.Labour;
import com.org.Entity.User;
import com.org.Repository.LabourRepository;
import com.org.Repository.UserRepository;
import com.org.Service.LabourService;

import jakarta.transaction.Transactional;

@Service
public class LabourServiceImpl implements LabourService{
	
	 private final LabourRepository labourRepository;
	 
	 private final UserRepository userRepo;

	LabourServiceImpl(LabourRepository labourRepository, UserRepository userRepo) {
		this.labourRepository = labourRepository;
		this.userRepo = userRepo;
	}
	 
	@Override
	public List<Labour> getAllLabours() {
	    // This will now compile successfully
	    return labourRepository.findAllByOrderByDateDescIdDesc();
	}	
	 
	 @Override
	 @Transactional
	 public void saveLabourEntry(Labour record, String email) {
	     
	     User user = userRepo.findByEmail(email);
	     
	     if (user == null) {
	         throw new RuntimeException("User not found: " + email);
	     }
	     record.setUser(user);

	     labourRepository.save(record);
	 }
	 
	 @Override
	 @Transactional
	 public boolean deleteById(Long id) {
	     // Check if the record exists in the database first
	     if (labourRepository.existsById(id)) {
	    	 labourRepository.deleteById(id);
	         return true; 
	     }
	     
	     return false;
	 }

}
