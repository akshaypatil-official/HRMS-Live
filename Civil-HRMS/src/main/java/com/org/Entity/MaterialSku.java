package com.org.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class MaterialSku {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(name = "material_sku", nullable = false, unique = true, length = 100)
	    private String materialSku;

	    public MaterialSku() {}

	    public MaterialSku(String materialSku) {
	        this.materialSku = materialSku;
	    }

	    public Long getId() { return id; }
	    public void setId(Long id) { this.id = id; }

	    public String getMaterialSku() { return materialSku; }
	    public void setMaterialSku(String materialSku) { this.materialSku = materialSku; }
}
