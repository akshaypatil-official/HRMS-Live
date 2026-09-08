package com.org.Entity;


import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "role")
public class Role {
    
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	   	    
	    private String name;
	    
	    @ManyToMany(mappedBy = "roles")
	    @JsonIgnoreProperties("roles") // Stops the loop back to user roles
	    private Collection<User> users;
	    
	    public Role() {
	    	
	    }
		public Role(String name) {
			super();
			this.name = name;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Collection<User> getUsers() {
			return users;
		}
		public void setUsers(Collection<User> users) {
			this.users = users;
		}
	
}
