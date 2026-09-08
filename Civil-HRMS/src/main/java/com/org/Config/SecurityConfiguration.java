package com.org.Config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.Service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
	private UserService userService;

	public SecurityConfiguration(UserService userService) {
        this.userService = userService;
        
    }
	 @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	            .csrf(csrf -> csrf.ignoringRequestMatchers("/material-ledger/**"))

	            .addFilterAfter(new OncePerRequestFilter() {
	                @Override
	                protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
	                                                jakarta.servlet.FilterChain filterChain)
	                        throws jakarta.servlet.ServletException, IOException {

	                    String path = request.getRequestURI();

	                    if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")
	                            || path.equals("/login") || path.equals("/force-logout") 
	                            || path.startsWith("/error") || path.equals("/favicon.ico")) {
	                        filterChain.doFilter(request, response);
	                        return;
	                    }

	                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	                    // Proper Anonymous check
	                    if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
	                        try {
	                            userService.loadUserByUsername(auth.getName());
	                        } catch (Exception e) { 
	                            HttpSession session = request.getSession(false);
	                            if (session != null) {
	                                session.invalidate();
	                            }
	                            SecurityContextHolder.clearContext();
	                            response.sendRedirect(request.getContextPath() + "/login?error=account_deleted");
	                            return;
	                        }
	                    }
	                    filterChain.doFilter(request, response);
	                }
	            }, AuthorizationFilter.class)

	            .authorizeHttpRequests((requests) -> requests
	                    .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/error", "/login", "/force-logout").permitAll()
	                    .requestMatchers("/material-ledger/**").authenticated()
	                    .requestMatchers("/employees").hasAnyAuthority("admin", "manager", "engineer")
	                    .anyRequest().authenticated())

	            .formLogin((form) -> form
	                    .loginPage("/login")
	                    .defaultSuccessUrl("/timesheets", true)
	                    .permitAll())

	            .logout(logout -> logout
	                    .logoutUrl("/logout")
	                    .logoutSuccessUrl("/login?logout")
	                    .invalidateHttpSession(true) 
	                    .deleteCookies("JSESSIONID") 
	                    .permitAll());

	        return http.build();
	    }
	 
	
}