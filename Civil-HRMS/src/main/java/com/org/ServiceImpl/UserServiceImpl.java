package com.org.ServiceImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.org.DTO.UserRegistrationDto;
import com.org.Entity.Company;
import com.org.Entity.Role;
import com.org.Entity.User;
import com.org.Repository.CompanyRepository;
import com.org.Repository.RoleRepository;
import com.org.Repository.UserRepository;
import com.org.Service.UserService;



@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    
    private final RoleRepository roleRepository;
    
    private CompanyRepository companyRepo;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,RoleRepository roleRepository,CompanyRepository companyRepo,@Lazy PasswordEncoder passwordEncoder) {
        super();
        this.userRepository = userRepository;
        this.roleRepository=roleRepository;
        this.companyRepo =companyRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override 
    public User save(UserRegistrationDto dto) {
        // 1. Fetch and validate the Role Entity
        Long roleId = Long.parseLong(dto.getRole_Id());
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role ID " + roleId + " not found"));
        
        // 2. Fetch and validate the Company Entity
        if (dto.getCompanyId() == null) {
            throw new IllegalArgumentException("Company selection is required.");
        }
        Company company = companyRepo.findById(dto.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Company ID " + dto.getCompanyId() + " not found"));
        
        // 3. Create a fresh User instance using an empty constructor
        User user = new User();
        
        // 4. Assign text and relation fields safely using Setters
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNo(dto.getPhoneNo());
        user.setDesignation(dto.getDesignation());
        user.setAddress(dto.getAddress());
        user.setGender(dto.getGender());
        user.setEmail(dto.getEmail());
        user.setCompany(company); // Links the full Company entity block safely
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // Encrypts the password
        user.setDob(dto.getDob());
        user.setDoj(dto.getDoj());
        // 6. Set the Role relationship list collection
        List<Role> rolesList = new ArrayList<>();
        rolesList.add(role);
        user.setRoles(rolesList);

        // 7. Save to database
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Fetch user by email from the database
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        // 2. Return the authenticated Spring Security user using your map helper
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), 
            user.getPassword(), 
            mapRolesToAuthorities(user.getRoles())
        );
    }

    // Your helper method is perfect - keeping it clean and robust
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        if (roles == null) {
            return new ArrayList<>();
        }
        return roles.stream()
                .filter(role -> role != null && role.getName() != null && !role.getName().trim().isEmpty())
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
    
    @Override
    public User getUserByEmail(String email) {
        // Calling your repository method
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            // Log it for debugging
            System.out.println("Service: User not found for email: " + email);
        }        
        return user;
    }
    

    @Override
    public String getFirstName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            User user = userRepository.findByEmail(email); // Returns User object
            if (user != null) {
                return user.getFirstName();
            }
        }
        return "Guest";
    }

    @Override
    public String getLastName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            User user = userRepository.findByEmail(email);
            if (user != null) {
                return user.getLastName();
            }
        }
        return "";
    }

	@Override
	public List<String> getAllActiveUsernames() {
		
		return null;
	}

	@Override
	public String getCompanyName(Long user_Id) {
	    // Fetches the company name via table join using the user's ID
	    return userRepository.findCompanyNameByUser_Id(user_Id); 
	}
	
}	