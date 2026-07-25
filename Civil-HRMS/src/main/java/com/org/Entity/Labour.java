package com.org.Entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name="labour_list")
public class Labour {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;	
	private LocalDate  date;
	String project_name;
	String site_name;
	String remark;
	private String photo;
	 @Column(name = "num_mistri")
	    private Integer numMistri = 0;

	    @Column(name = "num_fitter")
	    private Integer numFitter = 0;

	    @Column(name = "num_carpenter")
	    private Integer numCarpenter = 0;

	    @Column(name = "num_male_coolie")
	    private Integer numMaleCoolie = 0;

	    @Column(name = "num_female_coolie")
	    private Integer numFemaleCoolie = 0;
	
	    public void DailyLaborEntry() {
	    }
	    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getProject_name() {
		return project_name;
	}
	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}
	public String getSite_name() {
		return site_name;
	}
	public void setSite_name(String site_name) {
		this.site_name = site_name;
	}

	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getNumMistri() {
		return numMistri;
	}

	public void setNumMistri(Integer numMistri) {
		this.numMistri = numMistri;
	}

	public Integer getNumFitter() {
		return numFitter;
	}

	public void setNumFitter(Integer numFitter) {
		this.numFitter = numFitter;
	}

	public Integer getNumCarpenter() {
		return numCarpenter;
	}

	public void setNumCarpenter(Integer numCarpenter) {
		this.numCarpenter = numCarpenter;
	}

	public Integer getNumMaleCoolie() {
		return numMaleCoolie;
	}

	public void setNumMaleCoolie(Integer numMaleCoolie) {
		this.numMaleCoolie = numMaleCoolie;
	}

	public Integer getNumFemaleCoolie() {
		return numFemaleCoolie;
	}

	public void setNumFemaleCoolie(Integer numFemaleCoolie) {
		this.numFemaleCoolie = numFemaleCoolie;
	}
	
	
	
}
