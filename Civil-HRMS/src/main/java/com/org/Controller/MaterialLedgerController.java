package com.org.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.org.DTO.SiteBalanceReport;
import com.org.Entity.MaterialTransaction;
import com.org.Entity.User;
import com.org.Repository.UserRepository;
import com.org.Service.MaterialLedgerService;

@Controller
@RequestMapping("/material-ledger")
@CrossOrigin(origins = "*") 
public class MaterialLedgerController {
 
	private final MaterialLedgerService ledgerService;
    
	private final UserRepository userRepository;
	
    @Autowired
    public MaterialLedgerController(MaterialLedgerService ledgerService, UserRepository userRepository) {
        this.ledgerService = ledgerService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showMaterialsDashboard() {
        
        return "Material_Summary"; 
    }
    
    @PostMapping(value = "/transaction", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> postTransaction(
            @RequestPart("transaction") MaterialTransaction transaction,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            Principal principal) {
        
        if (transaction == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Validation Failure: Request body transaction details cannot be empty.");
        }

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication Failure: User session is missing or expired.");
        }

        String transactionType = transaction.getType() != null ? transaction.getType().toString() : "";
        
        if ("TRANSFER".equalsIgnoreCase(transactionType)) {
            String source = transaction.getSourceLocation();
            String destination = transaction.getDestinationLocation();

            if (source == null || destination == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Validation Failure: Source and destination locations are required for a TRANSFER.");
            }

            if (source.equalsIgnoreCase(destination)) {
                String validationMessage = "Validation Failure: Source and destination locations cannot match.";
    			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(validationMessage);
            }
        }

        try {
            // Query directly by the authenticated email string
            User loggedInUser = userRepository.findByEmail(principal.getName());

            if (loggedInUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Execution Failure: Logged-in employee details not found in database.");
            }

            // Apply clean relationship binding
            transaction.setUser(loggedInUser);
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    // 1. Define where you want to save the physical files
                    String uploadDir = "uploads/transactions/";
                    java.io.File directory = new java.io.File(uploadDir);
                    if (!directory.exists()) {
                        directory.mkdirs(); // Create the folder if it doesn't exist
                    }

                    // 2. Generate a unique name to prevent files from overwriting each other
                    String uniqueFileName = java.util.UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                    java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir, uniqueFileName);

                    // 3. Physically save the uploaded file to disk
                    java.nio.file.Files.copy(imageFile.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                    // 4. THIS STEP WAS MISSING: Update the entity with the generated file path/URI
                    transaction.setMaterialPhoto(filePath.toString());

                    System.out.println("Image saved successfully at: " + filePath.toString());

                } catch (java.io.IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Execution Failure: Failed to save uploaded image file. " + e.getMessage());
                }
            }

            // Now ledgerService will persist the transaction including the updated materialPhoto path
            ledgerService.logTransaction(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body("Transaction logged successfully.");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Execution Failure: " + e.getMessage());
        }
    }
    
    @GetMapping("/matrix")
    public ResponseEntity<List<SiteBalanceReport>> getBalancesMatrix(@RequestParam String materialSku) {
        return ResponseEntity.ok(ledgerService.computeBalancesMatrix(materialSku));
    }

    @Transactional(readOnly = true)
    @GetMapping("/history")
    public ResponseEntity<Page<MaterialTransaction>> getTransactionHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // ID किंवा Date नुसार DESC Sort (तुमच्या मॉडेलनुसार आयडीचे नाव तपासा)
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        
        // थेट Page ऑब्जेक्ट रिटर्न करा, List नाही!
        return ResponseEntity.ok(ledgerService.getAllTransactions(pageable));
    }
    
    @GetMapping("/sites")
    public ResponseEntity<List<String>> getSites() {
        return ResponseEntity.ok(ledgerService.getAllRegisteredSites());
    }

    @PostMapping("/site")
    public ResponseEntity<String> addSite(@RequestBody Map<String, String> payload) {
        String siteName = payload.get("name");
        if (siteName == null || siteName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Site name cannot be empty.");
        }
        ledgerService.registerSite(siteName.trim());
        return ResponseEntity.ok("Site registered successfully.");
    }

    @GetMapping("/materials")
    public ResponseEntity<List<String>> getMaterials() {
        return ResponseEntity.ok(ledgerService.getAllRegisteredMaterials());
    }

    @PostMapping("/material")
    public ResponseEntity<String> addMaterial(@RequestBody Map<String, String> payload) {
        String materialSku = payload.get("materialSku");
        if (materialSku == null || materialSku.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Material SKU cannot be empty.");
        }
        ledgerService.registerMaterial(materialSku.trim());
        return ResponseEntity.ok("Material SKU registered successfully.");
    }

    
    @DeleteMapping("/transaction/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId) {
        // Turn the Long back into a String just for the service layer method if needed
        boolean deleted = ledgerService.deleteTransactionById(String.valueOf(transactionId));
        
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
