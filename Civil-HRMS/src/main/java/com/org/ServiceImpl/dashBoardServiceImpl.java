package com.org.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.org.Entity.Timesheet;
import com.org.Repository.UserRepository;
import com.org.Repository.dashBoardRepository;
import com.org.Service.dashBoardService;

@Service
public class dashBoardServiceImpl implements dashBoardService{

	 private final dashBoardRepository dashBoardRepo;
	 
	 private final UserRepository userRepo;

	dashBoardServiceImpl(UserRepository userRepo, dashBoardRepository dashBoardRepo) {
		this.userRepo = userRepo;
		this.dashBoardRepo = dashBoardRepo;
	}
	
	 @Override
	    public List<Timesheet> getAttendanceByDate(LocalDate date) {
	        return dashBoardRepo.findByDate(date);
	    }

	 @Override
	    public int getTotalUsersCount() {
	        return userRepo.getTotalUserCount();
	    }
	 
	    @Override
	    public int getActiveCount(LocalDate date) {
	        return dashBoardRepo.countActiveNow(date);
	    }

	    @Override
	    public int getPendingCount(LocalDate date) {
	        // 1. Get total employees (e.g., 3)
	        int totalUsers = userRepo.getTotalUserCount();
	        
	        // 2. Get active employees using the pure LocalDate object (e.g., 1)
	        int activeUsers = dashBoardRepo.countActiveNow(date);
	        
	        // 3. Returns 3 - 1 = 2
	        return totalUsers - activeUsers;
	    }

}
