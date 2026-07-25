package com.org.Controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.org.Entity.User;
import com.org.Repository.EmployeeRepository;
import com.org.Service.EmployeeService;



@Controller
@RequestMapping("/employees")
public class EmployeeController {

	private final  EmployeeRepository empRepository;
	
	private final EmployeeService employeeService;


    EmployeeController(EmployeeService employeeService, EmployeeRepository empRepository) {
        this.employeeService = employeeService;
        this.empRepository = empRepository;
    }
	

	@GetMapping
    public String showEmployeeList(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        
        List<User> listEmployees;

        if (keyword != null && !keyword.isEmpty()) {
            // Search logic (requires a custom method in your Repository)
            listEmployees = empRepository.findByFirstNameContainingIgnoreCase(keyword);
        } else {
            // Fetch all employees from the database
            listEmployees = empRepository.findAll();
        }

        // Send the list to the HTML page
        model.addAttribute("listEmployees", listEmployees);
        model.addAttribute("keyword", keyword);
        System.out.print("employee list"+ listEmployees);
        return "employeelist"; 
    }
	

	 @GetMapping("/edit/{id}")
	    public String showEditForm(@PathVariable("id") Long user_id, Model model) {
	        User employee = employeeService.getUserById(user_id); 
	        model.addAttribute("employee", employee);
	        return "edit-employee"; 
	    }

	    // 3. Process the Edit Form Submission
	 @PostMapping("/update/{id}")
	 public String updateEmployee(@PathVariable("id") Long user_id, @ModelAttribute("user") User user) {
	     // Print incoming Path Variable
	     System.out.println("Updating User ID: " + user_id);
	     
	     // Print incoming Model Attribute data
	     System.out.println("Incoming User Details:");
	     System.out.println("First Name: " + user.getFirstName());
	     System.out.println("Last Name: " + user.getLastName());
	     System.out.println("Email: " + user.getEmail());
	     System.out.println("Phone: " + user.getPhoneNo());
	     System.out.println("Designation: " + user.getDesignation());
	     System.out.println("DOB: " + user.getDob());
	     System.out.println("DOJ: " + user.getDoj());
	     System.out.println("Aadhar: " + user.getAadharNO());
	     System.out.println("Address: " + user.getAddress());
	     System.out.println("PAN: " + user.getPanCardNo());
	     System.out.println("Gender: " + user.getGender());

	     employeeService.updateEmployee(user_id, user);
	     return "redirect:/employees"; 
	 }
	
	
	@GetMapping("/delete/{user_id}")
	public String deleteEmployee(@PathVariable("user_id") Long user_id) {
	    employeeService.deleteEmployee(user_id);
	    return "redirect:/employees";
	}
} 
