package com.org.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.org.Entity.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	User findByEmail(String email);
	
//	 @Procedure(procedureName = "emp_Timesheet_list")
//	    List<User> searchByNamesProc(@Param("firstName") String firstName, 
//	                                @Param("lastName") String lastName);

	    
	    @Query(value = "SELECT c.name FROM user u " +
                "JOIN companies c ON u.company_id = c.id " +
                "WHERE u.user_id = :user_id", nativeQuery = true)
 String findCompanyNameByUser_Id(@Param("user_id") Long user_Id);

	    
	    @Query(value = "SELECT u.user_id, u.first_name, u.last_name, u.email, " +
                "t.id, t.date, t.time_in, t.time_out, t.location, t.status, t.photo, t.out_photo " +
                "FROM user u " +
                "LEFT JOIN timesheet t ON u.user_id = t.user_id " +
                "WHERE CONCAT(TRIM(COALESCE(u.first_name, '')), ' ', TRIM(u.last_name)) " +
                "LIKE CONCAT('%', :searchTerm, '%') " +
                "ORDER BY t.date DESC", // CHANGED: Added descending sort by timesheet date
        nativeQuery = true)
List<Object[]> searchUsersWithTimesheets(@Param("searchTerm") String searchTerm);

	
@Query(value = "SELECT COUNT(user_id) FROM attendance_tracker.user", nativeQuery = true)
int getTotalUserCount();
}
