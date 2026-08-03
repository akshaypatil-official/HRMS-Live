package com.org.Entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Challans {
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	@Column(name = "challan_id")
	private long c_Id;

	@Column(name = "date")
	private LocalDate date;

	@Column(name = "company_name")
	private String CompanyName;
	
	@Column(name = "Project")
	private String ProjectName;

	@Column(name = "site_name")
	private String SiteName;
	
	@Column(name = "Vendor")
	private String VendorName;
	
	private String Status;

	@Column(name = "qty")
	private Double  Quantity;
	
	@Column(name = "unit")
	private String unit;
	
	@Column(name = "challan_no")
	private String ChallanNo;

	@Column(name = "in_time")
	private LocalTime InTime;
	
	@Column(name = "out_time")
	private LocalTime OutTime;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;


	private String VehicleNo;
	
	private String remark;
	
	private String challanPhoto;
	
	private String materialPhoto;
	
	private String VehiclePhoto;

	public long getC_Id() {
		return c_Id;
	}

	public void setC_Id(long c_Id) {
		this.c_Id = c_Id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getCompanyName() {
		return CompanyName;
	}

	public void setCompanyName(String companyName) {
		CompanyName = companyName;
	}

	public String getProjectName() {
		return ProjectName;
	}

	public void setProjectName(String projectName) {
		ProjectName = projectName;
	}

	public String getSiteName() {
		return SiteName;
	}

	public void setSiteName(String siteName) {
		SiteName = siteName;
	}

	public String getVendorName() {
		return VendorName;
	}

	public void setVendorName(String vendorName) {
		VendorName = vendorName;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public Double getQuantity() {
		return Quantity;
	}

	public void setQuantity(Double quantity) {
		Quantity = quantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getChallanNo() {
		return ChallanNo;
	}

	public void setChallanNo(String challanNo) {
		ChallanNo = challanNo;
	}

	public LocalTime getInTime() {
		return InTime;
	}

	public void setInTime(LocalTime inTime) {
		InTime = inTime;
	}

	public LocalTime getOutTime() {
		return OutTime;
	}

	public void setOutTime(LocalTime outTime) {
		OutTime = outTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getVehicleNo() {
		return VehicleNo;
	}

	public void setVehicleNo(String vehicleNo) {
		VehicleNo = vehicleNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getChallanPhoto() {
		return challanPhoto;
	}

	public void setChallanPhoto(String challanPhoto) {
		this.challanPhoto = challanPhoto;
	}

	public String getMaterialPhoto() {
		return materialPhoto;
	}

	public void setMaterialPhoto(String materialPhoto) {
		this.materialPhoto = materialPhoto;
	}

	public String getVehiclePhoto() {
		return VehiclePhoto;
	}

	public void setVehiclePhoto(String vehiclePhoto) {
		VehiclePhoto = vehiclePhoto;
	}

	
}
