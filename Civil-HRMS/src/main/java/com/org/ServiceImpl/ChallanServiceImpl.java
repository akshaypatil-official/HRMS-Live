package com.org.ServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.org.Entity.Challans;
import com.org.Entity.User;
import com.org.Repository.ChallansRepository;
import com.org.Repository.UserRepository;
import com.org.Service.ChallanService;

@Service
public class ChallanServiceImpl implements ChallanService {

	private final ChallansRepository challansRepo;

	private final String UPLOAD_DIR = "uploads/challan-photos/";
 
	private final UserRepository userRepo;
	
	ChallanServiceImpl(ChallansRepository challansRepo,UserRepository userRepo) {
		this.challansRepo = challansRepo;
		this.userRepo =userRepo;
	}
	
	
	@Override
	public List<Challans> getAllChallans() {
	    return challansRepo.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "date"));
	}


	 @Override
	 public void saveChallan(Challans challan, MultipartFile photoFile1, MultipartFile photoFile2, MultipartFile photoFile3, String username) {
	     try {
	         User user = userRepo.findByEmail(username); // or findByUsername
	         
	         if (user == null) {
	             throw new RuntimeException("User not found in database");
	         }
	         
	         // FIX: Call the method on the object instance (lowercase 'challan')
	         challan.setUser(user); 

	         // 1. Process and save Photo 1 (Challan Photo)
	         if (photoFile1 != null && !photoFile1.isEmpty()) {
	             String photo1Path = saveFile(photoFile1, "challan");
	             challan.setChallanPhoto(photo1Path); 
	         }

	         // 2. Process and save Photo 2 (Material Photo)
	         if (photoFile2 != null && !photoFile2.isEmpty()) {
	             String photo2Path = saveFile(photoFile2, "material");
	             challan.setMaterialPhoto(photo2Path); 
	         }

	         // 3. Process and save Photo 3 (Vehicle Photo)
	         if (photoFile3 != null && !photoFile3.isEmpty()) {
	             String photo3Path = saveFile(photoFile3, "vehicle");
	             challan.setVehiclePhoto(photo3Path); 
	         }

	         // 4. Save entity to the database via repository
	         challansRepo.save(challan);

	     } catch (IOException e) {
	         // Line-by-line logging is preferred over printStackTrace in production
	         throw new RuntimeException("Error occurred while saving file uploads: " + e.getMessage(), e);
	     }
	 }

	 private String saveFile(MultipartFile file, String prefix) throws IOException {
	     Path uploadPath = Paths.get(UPLOAD_DIR);

	     // Create folder if it doesn't exist
	     if (!Files.exists(uploadPath)) {
	         Files.createDirectories(uploadPath);
	     }

	     // Generate unique filename to avoid overwrites
	     String originalFilename = file.getOriginalFilename();
	     String uniqueFileName = prefix + "_" + UUID.randomUUID().toString() + "_" + originalFilename;

	     Path filePath = uploadPath.resolve(uniqueFileName);
	     
	     // Use REPLACE_EXISTING option to handle edge cases safely
	     Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	     // Return relative path string to save in DB
	     return filePath.toString();
	 }

	 @Override
	 public void deleteChallanById(Long id) {
	     // 1. Find the existing challan or throw an exception if it doesn't exist
	     Challans challan = challansRepo.findById(id)
	             .orElseThrow(() -> new RuntimeException("Challan not found with id: " + id));

	     // 2. Delete the physical files from the storage directory
	     deletePhysicalFile(challan.getChallanPhoto());
	     deletePhysicalFile(challan.getMaterialPhoto());
	     deletePhysicalFile(challan.getVehiclePhoto());

	     // 3. Delete the record from the database
	     challansRepo.deleteById(id);
	 }
	 // Helper method to safely delete files from disk
	 private void deletePhysicalFile(String filePathString) {
	     if (filePathString != null && !filePathString.isEmpty()) {
	         try {
	             Path filePath = Paths.get(filePathString);
	             // Delete only if the file actually exists on the disk
	             Files.deleteIfExists(filePath);
	         } catch (IOException e) {
	             // Log the error but do not block the DB deletion if file deletion fails
	             System.err.println("Failed to delete file at: " + filePathString + " Error: " + e.getMessage());
	         }
	     }
	 }


	
}
