package com.org.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.org.Entity.User;
import com.org.Repository.UserRepository;

@Controller
public class ProfileController {

	    private final UserRepository userRepository;

	ProfileController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	    @GetMapping("/profile")
	    public String showProfile(Authentication authentication, Model model) {
	        String email = authentication.getName();
	        User user = userRepository.findByEmail(email);
	        
	        model.addAttribute("user", user); // This sends data to HTML
	        System.out.println("user data show"+ user);
	        return "profile"; // This looks for profile.html
	    }
	}
