package com.org.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.org.Entity.Timesheet;

public interface dashBoardRepository extends JpaRepository<Timesheet, Long>{

	@Query("SELECT t FROM Timesheet t JOIN FETCH t.user WHERE t.date = :date")
    List<Timesheet> findByDate(@Param("date") LocalDate date);

	    // Counts total records matching the 'date' property
	   int countByDate(LocalDate date);

	    // Explicit custom queries using the 'date' property
	   @Query("SELECT COUNT(t) FROM Timesheet t WHERE t.date = :date")
	   int countActiveNow(@Param("date") LocalDate date);

	   
	    @Query("SELECT COUNT(t) FROM Timesheet t WHERE t.date >= :startOfDay AND t.date < :endOfDay")
	    int countActiveNow(
	        @Param("startOfDay") LocalDateTime startOfDay, 
	        @Param("endOfDay") LocalDateTime endOfDay
	    );
}
