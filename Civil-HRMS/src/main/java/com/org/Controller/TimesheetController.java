package com.org.Controller;


import java.io.IOException;
import java.security.Principal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.org.Entity.Timesheet;
import com.org.Entity.User;
import com.org.Excel.AttendanceSummaryExcelExporter;
import com.org.Excel.TimesheetExcelExporter;
import com.org.Service.TimesheetService;
import com.org.Service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@CrossOrigin
@RequestMapping("/timesheets")
public class TimesheetController {

	
	private final TimesheetService timesheetService;
	
	private final UserService userService;

    TimesheetController(TimesheetService timesheetService, UserService userService) {
        this.timesheetService = timesheetService;
        this.userService = userService;
    }
	
	    @GetMapping
	    public String showTimesheetPage(
	            Model model, 
	            Principal principal, 
	            @RequestParam(defaultValue = "0") int page) {
	        
	        int pageSize = 10;
	        Pageable pageable = PageRequest.of(page, pageSize);
	        
	        // 1. Declare and fetch the paginated page object using the principal name
	        Page<Timesheet> timesheetPage = timesheetService.getTimesheetsByUser(principal.getName(), pageable);
	        
	        // 2. Add structural pagination metadata to model
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", timesheetPage.getTotalPages());
	        
	        // 3. Extract and pass ONLY the list of 10 items for the current page
	        model.addAttribute("timesheets", timesheetPage.getContent());
	       
	        // 4. Pass empty object for your form binding
	        model.addAttribute("timesheet", new Timesheet()); 
	        
	        
	        return "Timesheet";
	    }
	

	    
    @PostMapping("/save")
    public String saveTimesheet(@ModelAttribute("timesheet") Timesheet timesheet, Principal principal) {
        if (principal == null) {
            return "redirect:/login"; // Redirect if session is lost
        }
        try {
            timesheetService.saveTimesheet(timesheet, principal.getName());
            return "redirect:/timesheets?success";
        } catch (Exception e) {
            return "redirect:/timesheets?error=" + e.getMessage();
        }
    }
      
    @GetMapping("/edit/{id}")
    @ResponseBody // Tells Spring to return raw data as JSON, not an HTML page view
    public ResponseEntity<Timesheet> getTimesheetData(@PathVariable("id") Long id) {
        // Find the single entry by its unique ID
        Timesheet timesheet = timesheetService.getTimesheetById(id); 
        
        if (timesheet == null) {
            return ResponseEntity.notFound().build(); // Sends a 404 error if not found
        }
        return ResponseEntity.ok(timesheet); // Sends a 200 OK status along with the entry data
    }
    
    @PostMapping("/update/{id}")
    public String updateTimesheet(
            @PathVariable("id") Long id, 
            @ModelAttribute("timesheet") Timesheet timesheet) {
        
        // Ensure the ID from the URL matches the object being saved
        timesheet.setId(id); 
        timesheetService.updateTimesheet(timesheet);
        
        return "redirect:/timesheets"; // Redirect back to the timesheet page
    }
    
    @GetMapping("/search") 
    @ResponseBody                      
    public List<Map<String, Object>> searchTimesheetsAjax(@RequestParam(value = "keyword") String keyword) {
        
        System.out.println("=== BACKEND SEARCH TRIGGERED ===");
        System.out.println("Search Keyword received: [" + keyword + "]");

        List<Object[]> rawResults = timesheetService.searchEmployeesByName(keyword);
        List<Map<String, Object>> response = new ArrayList<>();

        if (rawResults == null) {
            System.out.println("No results found or rawResults is null.");
            return response;
        }

        System.out.println("Total rows found in database: " + rawResults.size());
        System.out.println("--- PRINTING RAW SQL ROW ARRAYS ---");

        int rowIndex = 0;
        for (Object[] row : rawResults) {
            if (row == null) {
                System.out.println("Row [" + rowIndex + "] is null");
                rowIndex++;
                continue;
            }

            // ==========================================================
            // CRUCIAL ADDITION: Prints the exact array elements to your IDE/Server console
            // ==========================================================
            System.out.println("Row [" + rowIndex + "] Content: " + Arrays.toString(row));
            System.out.println("Row [" + rowIndex + "] Length: " + row.length);
            for (int col = 0; col < row.length; col++) {
                System.out.println("   -> Index [" + col + "]: " + (row[col] != null ? row[col] : "NULL") + " (" + (row[col] != null ? row[col].getClass().getSimpleName() : "void") + ")");
            }
            System.out.println("------------------------------------");

            Map<String, Object> map = new HashMap<>();
            
            // Explicit array indices mapping matching your specific database log positions
            if (row != null && row.length > 0 && row[0] != null) {
                Object idValue = row[0];
                if (idValue instanceof Number) {
                    // Safe extraction for standard Numeric/Long/Integer Primary Keys
                    map.put("id", String.valueOf(((Number) idValue).longValue()));
                } else {
                    // Safe extraction for Strings, UUIDs, or clean plain objects
                    map.put("id", idValue.toString().trim());
                }
            } else {
                map.put("id", "");
            }
            
            map.put("firstName", row.length > 1 && row[1] != null ? row[1].toString() : "");      
            map.put("lastName", row.length > 2 && row[2] != null ? row[2].toString() : "");
            map.put("timesheetId", row.length > 4 && row[4] != null ? row[4].toString() : "");
            map.put("date", row.length > 5 && row[5] != null ? row[5].toString() : "");      
            map.put("timeIn", row.length > 6 && row[6] != null ? row[6].toString() : "");    
            map.put("timeOut", row.length > 7 && row[7] != null ? row[7].toString() : "");   
            map.put("location", row.length > 8 && row[8] != null ? row[8].toString() : "Office"); 
            map.put("status", row.length > 9 && row[9] != null ? row[9].toString() : "Present");
            map.put("photo", row.length > 10 && row[10] != null ? row[10].toString() : "");
            map.put("outPhoto", row.length > 11 && row[11] != null ? row[11].toString() : "");
            

            response.add(map);
            rowIndex++;
        }
        
        System.out.println("=== END OF BACKEND DATA LOG ===");
        return response;
    }

    
    @GetMapping("/export")
    public void exportTimesheet(
        @RequestParam(name = "month", required = false) String month,
        HttpServletResponse response, 
        Principal principal
    ) throws IOException {
        
        // 1. Force login if session expired
        if (principal == null) {
            response.sendRedirect("/login");
            return;
        }
        
        // 2. Validate input format (expects YYYY-MM)
        if (month == null || month.trim().isEmpty() || !month.matches("^\\d{4}-\\d{2}$")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date format. Use YYYY-MM.");
            return;
        }

        // 3. Parse data securely
        String[] parts = month.split("-");
        int year = Integer.parseInt(parts[0]);
        int monthValue = Integer.parseInt(parts[1]);

        // 4. Get the logged-in Username/Email from principal
        String loggedInUserEmail = principal.getName(); 

        // 5. FETCH LOGIN USER ID: Use the email to get the actual User object from your database
        User loggedInUser = userService.getUserByEmail(loggedInUserEmail); // Or findByUsername depending on your setup
        
        if (loggedInUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User details not found.");
            return;
        }
        
        Long user_Id = loggedInUser.getUser_id(); // This extracts the actual logged-in user's ID dynamically

        // 6. Fetch ONLY filtered rows from the database
        List<Timesheet> listTimesheets = timesheetService.findByUserAndMonth(loggedInUserEmail, year, monthValue); 

        // 7. Fetch details using the dynamically retrieved user_Id
        String companyName = userService.getCompanyName(user_Id); 
        if (companyName == null) {
            companyName = "Default Company";
        }

        String firstName = loggedInUser.getFirstName(); // Safer to pull directly from the retrieved user object
        String lastName = loggedInUser.getLastName();
        
        if (firstName == null) firstName = "User";
        if (lastName == null) lastName = "";

        String loggedInUserName = (firstName + " " + lastName).trim();
        String safeFileName = loggedInUserName.replaceAll("[^a-zA-Z0-9.-]", "_");
        String finalFileName = "Timesheet_" + safeFileName + "_" + month + ".xlsx";
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + finalFileName + "\"");

        TimesheetExcelExporter exporter = new TimesheetExcelExporter(listTimesheets,companyName, loggedInUserName, month);
        exporter.export(response);
    }

    @GetMapping("/attendance-summary/export")
    public void exportAttendanceSummary(
            @RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month, 
            HttpServletResponse response) throws IOException {
        
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "Attendance_Summary_" + month.toString() + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            
            List<Timesheet> summaryList = timesheetService.getAllEmployeesTimesheet(month);
            
            // Check if data retrieval returned null
            if (summaryList == null) {
                throw new RuntimeException("Timesheet service returned null data.");
            }

            String companyName = ""; // Ensure your exporter handles empty strings safely
            
            AttendanceSummaryExcelExporter exporter = new AttendanceSummaryExcelExporter(
                    summaryList, 
                    companyName, 
                    month.toString()
            );
                    
            exporter.export(response);
            
        } catch (Exception e) {
            // This will print the actual error stack trace to your IDE/server console
            e.printStackTrace(); 
            
            // Reset response to send a clean error message instead of a broken stream
            if (!response.isCommitted()) {
                response.reset();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            }
        }
    }

}
    
