package com.org.Controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.org.Entity.Timesheet;
import com.org.Entity.User;
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
        
        // 1. Fetch data from services
        List<Timesheet> activities = dashBoardServ.getAttendanceByDate(dateSearch);
        int totalUsers = dashBoardServ.getTotalUsersCount();
        int pendingApprovals = dashBoardServ.getPendingCount(dateSearch);
        List<User> allEmployees = dashBoardServ.getAllUsers(); 

        // 2. Extract active IDs into an effectively final Set right away
        final Set<Long> finalActiveIds = (activities == null) ? new HashSet<>() : 
            activities.stream()
                .filter(activity -> activity.getUser() != null)
                .map(activity -> activity.getUser().getUser_id()) // Ensure getId() returns Long
                .collect(Collectors.toSet());

        // 3. Find employees who DID NOT fill attendance at all today
        List<String> missingAttendanceNames = new ArrayList<>();
        if (allEmployees != null) {
            missingAttendanceNames = allEmployees.stream()
                .filter(emp -> !finalActiveIds.contains(emp.getUser_id()))
                .map(emp -> emp.getFirstName() + " " + emp.getLastName())
                .collect(Collectors.toList());
        }

        // 4. Find employees who filled attendance but status is still null
        List<String> pendingApprovalNames = new ArrayList<>();
        if (activities != null) {
            pendingApprovalNames = activities.stream()
                .filter(activity -> activity.getStatus() == null && activity.getUser() != null)
                .map(activity -> activity.getUser().getFirstName() + " " + activity.getUser().getLastName())
                .collect(Collectors.toList());
        }

        // 5. Combine lists cleanly
        List<String> totalPendingActionList = new ArrayList<>();
        totalPendingActionList.addAll(missingAttendanceNames);
        totalPendingActionList.addAll(pendingApprovalNames);

        // 6. Bind to UI layout
        model.addAttribute("activities", activities);
        model.addAttribute("totalEmployees", totalUsers);
        model.addAttribute("pendingApprovals", pendingApprovals);
        model.addAttribute("currentSearchDate", dateSearch);
        model.addAttribute("pendingEmployees", totalPendingActionList); 

        return "dash-Board";
    }


}
