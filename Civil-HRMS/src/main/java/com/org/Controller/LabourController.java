package com.org.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.org.Entity.Labour; 
import com.org.Service.LabourService;


@Controller
@RequestMapping("/labourRecords")
public class LabourController {
    
    private final LabourService labourService;

    private final String UPLOAD_DIR = "uploads/";
    
    LabourController(LabourService labourService) {
        this.labourService = labourService;
    }

    @GetMapping
    public String showForm(Model model, Principal principal) {
        System.out.println("--- Fetching All Application Labour Data ---");
        
        java.util.List<Labour> allLabourRecords = labourService.getAllLabours();
        
        // Print total count to your IDE terminal for verification
        if (allLabourRecords != null) {
            System.out.println("Total Global Labour Records Found: " + allLabourRecords.size());
        } else {
            System.out.println("Warning: Global Labour dataset returned null.");
        }
        
        // 2. Bind the global list to your Thymeleaf layout model
        model.addAttribute("allLabours", allLabourRecords);
        
        // 3. Provide an empty blueprint object for your submission form
        model.addAttribute("labour", new Labour());    
        
        System.out.println("--------------------------------------------");
        return "Labour_Form";
    }

    @PostMapping("/saveLabour")
    public String saveRecord(@ModelAttribute("labour") Labour labour,
                             @RequestParam("photoFile") MultipartFile photoFile,
                             Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        try {

            String uploadDir = "uploads/";

            // Create upload folder if it doesn't exist
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Upload photo
            if (!photoFile.isEmpty()) {

                String fileName = System.currentTimeMillis() + "_"
                        + photoFile.getOriginalFilename();

                Path path = Paths.get(uploadDir, fileName);

                Files.copy(photoFile.getInputStream(),
                        path,
                        StandardCopyOption.REPLACE_EXISTING);

                // Save image path in database
                labour.setPhoto("/uploads/" + fileName);
            }

            // Save labour record
            labourService.saveLabourEntry(labour, principal.getName());

            return "redirect:/labourRecords?success";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/labourRecords?error";
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecord(@PathVariable Long id) {
        boolean isDeleted = labourService.deleteById(id);
        
        if (isDeleted) {
            return ResponseEntity.ok("Record deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Delete failed: Record not found with ID: " + id);
        }
    }
}

