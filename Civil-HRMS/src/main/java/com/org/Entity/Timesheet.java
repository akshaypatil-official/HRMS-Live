package com.org.Entity;

import java.time.LocalDate;
import java.time.LocalTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.ForeignKey;

@Entity
@Table(name = "timesheet")
public class Timesheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private LocalTime timeIn;
    private LocalTime timeOut;
    private LocalTime nightTimeIn;
    private LocalTime nightTimeOut;
    private String status;
    @Column(name = "night_status")
    private String nightStatus; 
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String photo;
    private String location;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FKm9k943y6g38ujtoa9na8bseak"))
    @OnDelete(action = OnDeleteAction.CASCADE) // <-- ADD THIS TO FORCE DATABASE CASCADE
    private User user;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String outPhoto;
    
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

	public LocalTime getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(LocalTime timeIn) {
		this.timeIn = timeIn;
	}

	public LocalTime getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(LocalTime timeOut) {
		this.timeOut = timeOut;
	}

	public LocalTime getNightTimeIn() {
		return nightTimeIn;
	}

	public void setNightTimeIn(LocalTime nightTimeIn) {
		this.nightTimeIn = nightTimeIn;
	}

	public LocalTime getNightTimeOut() {
		return nightTimeOut;
	}

	public void setNightTimeOut(LocalTime nightTimeOut) {
		this.nightTimeOut = nightTimeOut;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNightStatus() {
		return nightStatus;
	}

	public void setNightStatus(String nightStatus) {
		this.nightStatus = nightStatus;
	}

	public String getOutPhoto() {
		return outPhoto;
	}

	public void setOutPhoto(String outPhoto) {
		this.outPhoto = outPhoto;
	}
        
}
