package com.org.Controller;

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

import com.org.Entity.Challans;
import com.org.Service.ChallanService;


@Controller
@RequestMapping("/challans")
public class AllChallanController {
  
	private final ChallanService challanService;

	AllChallanController(ChallanService challanService) {
		this.challanService = challanService;
	}
	
	    @GetMapping
	    public String showChallanForm(Model model) {
	        List<Challans> challansList = challanService.getAllChallans();
	        model.addAttribute("challans", challansList);
	        model.addAttribute("newChallan", new Challans());
	        return "Challans"; 
	    }
	
	@PostMapping("/save")
    public String saveChallan(@ModelAttribute("newChallan") Challans challan,
    		@RequestParam("photoFile1") MultipartFile photoFile1, 
    	    @RequestParam("photoFile2") MultipartFile photoFile2,
    	    @RequestParam("photoFile3") MultipartFile photoFile3,
    	    Principal principal) {
		 if (principal == null) {
		        return "redirect:/login"; 
		    }
		    
        challanService.saveChallan(challan, photoFile1, photoFile2, photoFile3,principal.getName()); 
        return "redirect:/challans"; 	
    }
	
	@DeleteMapping("/delete/{id}")
	@ResponseBody 
    public String deleteChallan(@PathVariable("id") Long id) {
        challanService.deleteChallanById(id);
        return "redirect:/challans";
    }
}
