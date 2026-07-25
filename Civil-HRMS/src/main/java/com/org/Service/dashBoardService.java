package com.org.Service;

import java.time.LocalDate;
import java.util.List;

import com.org.Entity.Timesheet;

public interface dashBoardService {

	List<Timesheet> getAttendanceByDate(LocalDate dateSearch);

	int getActiveCount(LocalDate dateSearch);

	int getPendingCount(LocalDate dateSearch);

	int getTotalUsersCount();

}
