package com.org.Entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.org.Enum.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "material_transactions")
public class MaterialTransaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "transaction_date", nullable = false)
	private LocalDate date;

	@Column(name = "challan_ref", nullable = false, length = 50)
	private String challanRef;

	@Column(name = "material_sku", nullable = false, length = 100)
	private String materialSku;

	@Enumerated(EnumType.STRING)
	@Column(name = "transaction_type", nullable = false, length = 20)
	private TransactionType type;

	@Column(name = "source_location", nullable = false, length = 150)
	private String sourceLocation;

	@Column(name = "destination_location", nullable = false, length = 150)
	private String destinationLocation;

	@Column(nullable = false)
	private double quantity;

	@Column(length = 500)
	private String remarks;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"transactions", "roles", "password"}) // Safely ignores circular dependencies
    private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id") // or whatever your database column is named
	@JsonIgnoreProperties({"transactions", "users", "employees"}) // Stops Jackson from looping back
	private Company company;
	
	public MaterialTransaction() {
	}

	public MaterialTransaction(Long id, LocalDate date, String challanRef, String materialSku, TransactionType type,
			String sourceLocation, String destinationLocation, double quantity, String remarks, User user) {
		super();
		this.id = id;
		this.date = date;
		this.challanRef = challanRef;
		this.materialSku = materialSku;
		this.type = type;
		this.sourceLocation = sourceLocation;
		this.destinationLocation = destinationLocation;
		this.quantity = quantity;
		this.remarks = remarks;
		this.user = user;

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getChallanRef() {
		return challanRef;
	}

	public void setChallanRef(String challanRef) {
		this.challanRef = challanRef;
	}

	public String getMaterialSku() {
		return materialSku;
	}

	public void setMaterialSku(String materialSku) {
		this.materialSku = materialSku;
	}

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public String getSourceLocation() {
		return sourceLocation;
	}

	public void setSourceLocation(String sourceLocation) {
		this.sourceLocation = sourceLocation;
	}

	public String getDestinationLocation() {
		return destinationLocation;
	}

	public void setDestinationLocation(String destinationLocation) {
		this.destinationLocation = destinationLocation;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}


	
	
}
