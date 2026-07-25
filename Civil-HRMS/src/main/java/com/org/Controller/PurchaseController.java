	package com.org.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.org.Entity.Purchase;
import com.org.Excel.MaterialExcelExporter;
import com.org.Service.PurchaseService;

import jakarta.servlet.http.HttpServletResponse;



@Controller
@RequestMapping("/purchases")
public class PurchaseController {

	 private final PurchaseService purchaseService;


	PurchaseController(PurchaseService purchaseService) {
		this.purchaseService = purchaseService;
	}
	 
	    
	@GetMapping
	public String listPurchases(Model model) {
	    // 1. Fetch your list data
	    List<Purchase> allPurchases = purchaseService.getAllPurchases();
	    model.addAttribute("purchases", allPurchases);
	    
	    // 2. FIX: Add this line so the form binding doesn't crash
	    model.addAttribute("purchase", new Purchase()); 
	    
	    return "Purchase"; // Replace with your exact template file name
	}
	    
	    
	    
	@PostMapping("/save") 
	public String savePurchase(
	    @ModelAttribute("purchase") Purchase purchase, 
	    @RequestParam("photoFile1") MultipartFile photoFile1, 
	    @RequestParam("photoFile2") MultipartFile photoFile2, 
	    Principal principal
	) { 
	    if (principal == null) {
	        return "redirect:/login"; 
	    }
	    
	    try { 
	        String uploadDir = "uploads/"; 
	        File dir = new File(uploadDir); 
	        if (!dir.exists()) { 
	            dir.mkdirs(); 
	        } 

	        // Handle first photo
	        if (!photoFile1.isEmpty()) { 
	            String fileName1 = System.currentTimeMillis() + "_1_" + photoFile1.getOriginalFilename(); 
	            Path path1 = Paths.get(uploadDir, fileName1); 
	            Files.copy(photoFile1.getInputStream(), path1, StandardCopyOption.REPLACE_EXISTING); 
	            // FIXED: Saves the exact file path to the database instead of just "uploads/"
	            purchase.setInvicePhoto("/uploads/" + fileName1);
	        } 

	        // Handle second photo
	        if (!photoFile2.isEmpty()) { 
	            String fileName2 = System.currentTimeMillis() + "_2_" + photoFile2.getOriginalFilename(); 
	            Path path2 = Paths.get(uploadDir, fileName2); 
	            Files.copy(photoFile2.getInputStream(), path2, StandardCopyOption.REPLACE_EXISTING); 
	            // FIXED: Saves the exact file path to the database instead of just "uploads/"
	            purchase.setMaterialPhoto("/uploads/" + fileName2);
	        } 

	        purchaseService.savePurchase(purchase, principal.getName()); 
	        return "redirect:/purchases?success"; 
	        
	    } catch (Exception e) { 
	        e.printStackTrace(); 
	        return "redirect:/purchases?error=db_error"; 
	    } 
	}


	    @GetMapping("/export-excel")
	    public void exportToExcel(HttpServletResponse response) throws IOException {
	        response.setContentType("application/octet-stream");
	        response.setHeader("Content-Disposition", "attachment; filename=MaterialList.xlsx");
	        
	        List<Purchase> listPurchases = purchaseService.findAll(); // Get your data
	        MaterialExcelExporter exporter = new MaterialExcelExporter(listPurchases);
	        exporter.export(response);
	    }
	    
	    
	    @GetMapping("/delete/{id}")
	    @ResponseBody
	    public String delete(@PathVariable long id) {
	        purchaseService.deletePurchase(id);
	        return "Deleted successfully";
	    }
}
