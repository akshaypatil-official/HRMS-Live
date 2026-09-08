package com.org.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.Entity.ProjectSite;


@Repository
public interface ProjectSiteRepository extends JpaRepository<ProjectSite, Long>{
	boolean existsByNameIgnoreCase(String name);
}