package com.org.Controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.org.Entity.Timesheet;
import com.org.Service.dashBoardService;

@Controller
@RequestMapping("/dashBoard")
public class dashBoardController {

	private final dashBoardService dashBoardServ;

    dashBoardController(dashBoardService dashBoardServ) {
        this.dashBoardServ = dashBoardServ;
    }
	
    @GetMapping 
    public String dashBoard(
        @RequestParam(value = "dateSearch", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateSearch, 
        Model model) {
        
        if (dateSearch == null) {
            dateSearch = LocalDate.now();
        }
        
        // --- PRINT INPUT VARIABLE ---
        System.out.println(">>> Dashboard Requested for Date: " + dateSearch);

        // Fetch filtered values from service
        List<Timesheet> activities = dashBoardServ.getAttendanceByDate(dateSearch);
        int totalUsers = dashBoardServ.getTotalUsersCount();
        int pendingApprovals = dashBoardServ.getPendingCount(dateSearch);
        
        // NEW: Fetch all timesheet records
        

        // --- PRINT FETCHED VALUES ---
        System.out.println(">>> Service Data Fetched successfully:");
        System.out.println(" Total Activities Found: " + (activities != null ? activities.size() : 0));
        
        if (activities != null) {
            for (Timesheet activity : activities) {
                if (activity.getUser() != null) {
                    System.out.println("  Employee: " + activity.getUser().getFirstName() + " " + activity.getUser().getLastName());
                } else {
                    System.out.println("  Employee: [No User linked to this timesheet record]");
                }
                System.out.println("  -> Photo Path: " + activity.getPhoto());
                System.out.println("  -> OutPhoto Path: " + activity.getOutPhoto());
            }
        }
        System.out.println(" Total Employees Count: " + totalUsers);
        System.out.println(" Pending Approvals Count: " + pendingApprovals);

        // Add to model
        model.addAttribute("activities", activities);
        model.addAttribute("totalEmployees", totalUsers);
        model.addAttribute("pendingApprovals", pendingApprovals);
        model.addAttribute("currentSearchDate", dateSearch);
     

        return "dash-Board";
    }


}
