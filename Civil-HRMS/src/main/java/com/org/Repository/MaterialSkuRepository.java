package com.org.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.Entity.MaterialSku;

@Repository
public interface MaterialSkuRepository extends JpaRepository<MaterialSku, Long>{

	boolean existsByMaterialSkuIgnoreCase(String materialSku);

}