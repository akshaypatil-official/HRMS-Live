package com.org.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.org.Entity.Timesheet;
import com.org.Entity.User;
import com.org.Repository.TimesheetRepository;
import com.org.Repository.UserRepository;
import com.org.Service.TimesheetService;
import jakarta.transaction.Transactional;


@Service
public class TimesheetServiceImpl implements TimesheetService{

	 private final TimesheetRepository timesheetRepo;

	    private final UserRepository userRepo;

     TimesheetServiceImpl(TimesheetRepository timesheetRepo, UserRepository userRepo) {
          this.timesheetRepo = timesheetRepo;
          this.userRepo = userRepo;
     }

     @Override 
     public Page<Timesheet> getTimesheetsByUser(String email, Pageable pageable) { 
         User user = userRepo.findByEmail(email); 
         if (user == null) { 
             return Page.empty(pageable); 
         } 

         // Auto-create Absent or Sunday records for previous 2 days 
         LocalDate today = LocalDate.now(); 
         for (int i = 1; i <= 2; i++) { 
             LocalDate checkDate = today.minusDays(i); 
             boolean exists = timesheetRepo.existsByUserAndDate(user, checkDate); 
             if (!exists) { 
                 Timesheet absentRecord = new Timesheet(); 
                 absentRecord.setUser(user); 
                 absentRecord.setDate(checkDate); 

                 // --- FIXED: SUNDAY AUTO FILL CHECK ---
                 if (checkDate.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
                     absentRecord.setStatus("Sunday"); 
                 } else {
                     absentRecord.setStatus("Absent"); 
                 }
                 // -------------------------------------

                 absentRecord.setTimeIn(LocalTime.of(0, 0)); 
                 absentRecord.setTimeOut(LocalTime.of(0, 0)); 
                 timesheetRepo.save(absentRecord); 
             } 
         } 

         LocalDate startDate = today.minusDays(39); 
         
         // CHANGED: Changed .ascending() to .descending()
         Pageable sortedByDate = PageRequest.of( 
             pageable.getPageNumber(), 
             pageable.getPageSize(), 
             Sort.by("date").descending() 
         ); 

         return timesheetRepo.findByUserAndDateBetween( 
             user, 
             startDate, 
             today, 
             sortedByDate 
         ); 
     }


     @Override
     @Transactional
     public void saveTimesheet(Timesheet timesheet, String email) {
         // 1. Identify the user
         User user = userRepo.findByEmail(email);
         if (user == null) {
             throw new RuntimeException("User not found: " + email);
         }

         LocalDate today = LocalDate.now();

         // 2. AUTO-ABSENT LOGIC: Only trigger this during Morning Check-In (when no entry exists for today)
         List<Timesheet> existingRows = timesheetRepo.findByUserAndDate(user, today);
         
         if (existingRows.isEmpty()) {
             Timesheet latestEntry = timesheetRepo.findTopByUserOrderByDateDesc(user);
             
             if (latestEntry != null) {
                 LocalDate lastSavedDate = latestEntry.getDate();
                 long missingDays = ChronoUnit.DAYS.between(lastSavedDate, today);

                 if (missingDays > 1) {
                     for (int i = 1; i < missingDays; i++) {
                         LocalDate gapDate = lastSavedDate.plusDays(i);
                         
                         Timesheet absentRecord = new Timesheet();
                         absentRecord.setUser(user);
                         absentRecord.setDate(gapDate);
                         absentRecord.setStatus("Absent");
           
                         timesheetRepo.save(absentRecord);
                     }
                 }
             }
         }

         // 3. EVENING LOGIC: Update timeOut if record exists for today
         if (!existingRows.isEmpty()) {
             Timesheet existingRecord = existingRows.get(0);

             if (timesheet.getTimeOut() != null) {
                 existingRecord.setTimeOut(timesheet.getTimeOut());
                 existingRecord.setOutPhoto(timesheet.getOutPhoto());

                 // --- DIRECT COMPARISON (No parsing needed) ---
                 java.time.LocalTime deadline = java.time.LocalTime.of(15, 0); // 3:00 PM

                 if (timesheet.getTimeOut().isBefore(deadline)) {
                     existingRecord.setStatus("Half Day");
                 } else {
                     existingRecord.setStatus("Present"); // Keeps it Present if 3 PM or later
                 }
                 // ----------------------------------------------
             }
             if (timesheet.getNightTimeOut() != null) {
                 existingRecord.setNightTimeOut(timesheet.getNightTimeOut());
                 existingRecord.setNightStatus(timesheet.getNightStatus()); 
                 existingRecord.setStatus(timesheet.getStatus());           
             }

             if (timesheet.getNightTimeIn() != null && existingRecord.getNightTimeIn() == null) {
                 existingRecord.setNightTimeIn(timesheet.getNightTimeIn());
                 existingRecord.setNightStatus(timesheet.getNightStatus());
                 existingRecord.setStatus(timesheet.getStatus());
             }
             
             timesheetRepo.save(existingRecord);
             
         } else {
             // 4. MORNING LOGIC: Create the primary entry for today
             timesheet.setUser(user);
             timesheet.setDate(today);
             
             if ("DayNight".equals(timesheet.getStatus())) {
                 timesheet.setTimeIn(null);
                 timesheet.setTimeOut(null);
             } else {
                 if (timesheet.getStatus() == null) {
                     timesheet.setStatus("Present");
                 }
                 timesheet.setNightStatus(null);
                 timesheet.setNightTimeIn(null);
                 timesheet.setNightTimeOut(null);
             }
             
             timesheetRepo.save(timesheet);
         }
     }
		@Override
		public List<Timesheet> findAll() {
			// TODO Auto-generated method stub
			 return timesheetRepo.findAll();
		}

		@Override
		public boolean hasUserLoggedTimeForDate(String email, LocalDate today) {
		    // 1. Find the user by email first
		    User user = userRepo.findByEmail(email);
		    
		    // 2. If the user doesn't exist, they haven't logged time
		    if (user == null) {
		        return false;
		    }
		    
		    // 3. Query the repository using the User object and the date
		    return timesheetRepo.existsByUserAndDate(user, today);
		}

		@Override
	    public List<Timesheet> findByUserAndMonth(String userId, int year, int month) {
	        // Logs the parameters to the console for easy debugging
	        System.out.println("Service Layer -> Fetching data for User: " + userId + ", Year: " + year + ", Month: " + month);
	        
	        // Sends the parameters straight to the database query
	        return timesheetRepo.findByUserAndMonth(userId, year, month);
	    }

		@Override
		@Transactional // Ensures the update operation runs safely inside a database transaction
		public void updateTimesheet(Timesheet timesheet) {
		    // 1. Verify that the timesheet exists in the database first
		    Timesheet existingTimesheet = timesheetRepo.findById(timesheet.getId())
		            .orElseThrow(() -> new IllegalArgumentException("Timesheet not found with ID: " + timesheet.getId()));
		    
		    // 2. Map the updated values from the form to the database entity
		    // Replace these field names with the exact field names in your Timesheet class
		    existingTimesheet.setDate(timesheet.getDate());
		    existingTimesheet.setLocation(timesheet.getLocation());
		    existingTimesheet.setStatus(timesheet.getStatus());
		    existingTimesheet.setTimeIn(timesheet.getTimeIn());
		    existingTimesheet.setTimeOut(timesheet.getTimeOut());
		    existingTimesheet.setNightTimeIn(timesheet.getNightTimeIn());
		    existingTimesheet.setNightTimeOut(timesheet.getNightTimeOut());
		    existingTimesheet.setNightStatus(timesheet.getNightStatus());
		    
		    // If your Timesheet has a relationship with a User, retain it so it doesn't become null
		    if (timesheet.getUser() != null) {
		        existingTimesheet.setUser(timesheet.getUser());
		    }

		    // 3. Save the modified entity back to the database
		    timesheetRepo.save(existingTimesheet);
		}

		@Override
		public Timesheet getTimesheetById(Long id) {
		    // Finds the timesheet by ID, or returns null if it does not exist
		    return timesheetRepo.findById(id).orElse(null);
		}

		@Override
	    public List<Object[]> searchEmployeesByName(String keyword) {
	        // If search input is null or completely blank, return an empty list or fetch all records
	        if (keyword == null || keyword.trim().isEmpty()) {
	            return new ArrayList<>(); 
	        }

	        // Clean up the search string: convert multiple spaces into a single space
	        String cleanedSearchTerm = keyword.trim().replaceAll("\\s+", " ");

	        // Execute the native SQL search via the repository
	        return userRepo.searchUsersWithTimesheets(cleanedSearchTerm);
	    }

	    
	    @Override
		public List<Timesheet> getAllEmployeesTimesheet(YearMonth month) {
		    LocalDate startDate = month.atDay(1);
		    LocalDate endDate = month.atEndOfMonth();
		    
		    // 1. Fetch all raw daily entries for all employees
		    List<Timesheet> rawTimesheets = timesheetRepo.findByMonthRange(startDate, endDate);
		    
		    // 2. Group the records by Employee ID to prevent duplicate name rows
		    Map<Object, List<Timesheet>> groupedByUser = rawTimesheets.stream()
		        .filter(t -> t.getUser() != null)
		        .collect(Collectors.groupingBy(t -> t.getUser().getEmail()));
		        
		    List<Timesheet> consolidatedList = new ArrayList<>();
		    
		    // 3. Merge daily records into a single summary Timesheet object per user
		    for (Entry<Object, List<Timesheet>> entry : groupedByUser.entrySet()) {
		        List<Timesheet> userRecords = entry.getValue();
		        
		        // Take the first record as a baseline container for User and Company details
		        Timesheet summaryRecord = new Timesheet();
		        summaryRecord.setUser(userRecords.get(0).getUser());
		        
		        // Combine all daily statuses into a single comma-separated string
		        String combinedStatuses = userRecords.stream()
		            .map(Timesheet::getStatus)
		            .filter(Objects::nonNull)
		            .collect(Collectors.joining(", "));
		            
		        // Combine all daily night statuses into a single comma-separated string
		        String combinedNightStatuses = userRecords.stream()
		            .map(Timesheet::getNightStatus)
		            .filter(Objects::nonNull)
		            .collect(Collectors.joining(", "));
		            
		        summaryRecord.setStatus(combinedStatuses);
		        summaryRecord.setNightStatus(combinedNightStatuses);
		        
		        consolidatedList.add(summaryRecord);
		    }
		    
		    return consolidatedList;
		}
}
