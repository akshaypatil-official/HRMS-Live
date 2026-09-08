package com.org.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    
    @PostMapping(value = "/transaction", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postTransaction(@RequestBody MaterialTransaction transaction, Principal principal) {
        
        if (transaction == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Validation Failure: Request body cannot be empty.");
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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Validation Failure: Source and destination locations cannot match.");
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

    @GetMapping("/history")
    public ResponseEntity<List<MaterialTransaction>> getTransactionHistory() {
        return ResponseEntity.ok(ledgerService.getAllTransactions());
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
