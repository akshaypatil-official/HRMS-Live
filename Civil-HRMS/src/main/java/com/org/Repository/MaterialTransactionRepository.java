package com.org.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.org.Entity.MaterialTransaction;

public interface MaterialTransactionRepository extends JpaRepository<MaterialTransaction, Long>{

	List<MaterialTransaction> findAllByOrderByDateDesc();
	
	@Query("SELECT DISTINCT t.sourceLocation FROM MaterialTransaction t WHERE t.type = 'TRANSFER' OR t.type = 'CONSUMPTION' " +
	           "UNION " +
	           "SELECT DISTINCT t.destinationLocation FROM MaterialTransaction t WHERE t.type = 'TRANSFER' OR t.type = 'INWARD'")
	
	    List<String> findDistinctProjectSites();

	
}