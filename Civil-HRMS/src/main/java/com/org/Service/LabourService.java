package com.org.Service;



import java.util.List;
import com.org.Entity.Labour;


public interface LabourService {


	 void saveLabourEntry(Labour record, String email);

	 List<Labour> getAllLabours();

	 boolean deleteById(Long id);

	
}
