package com.org.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.org.Entity.Challans;

@Repository
public interface ChallansRepository extends JpaRepository<Challans, Long>{

	List<Challans> findAll();

}
