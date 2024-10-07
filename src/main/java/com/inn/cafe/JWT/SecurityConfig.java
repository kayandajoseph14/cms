package com.inn.cafe.JWT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration // Marks this class as a source of Spring configuration
@EnableWebSecurity // Enables Spring Security for the application
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomerUsersDetailsService customerUsersDetailsService;  // Injects the custom UserDetailsService for user authentication

    @Autowired
    JwtFilter jwtFilter; // Injects the JWT filter to validate JWT tokens in requests

    /**
     * Configures Spring Security to use a custom UserDetailsService for authentication.
     * The UserDetailsService is responsible for loading user-specific data.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customerUsersDetailsService);  // Uses the custom UserDetailsService
    }

    /**
     * Defines the PasswordEncoder bean.
     * Currently using NoOpPasswordEncoder (not secure, use BCryptPasswordEncoder for production).
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance(); // No password encoding (for testing purposes, not recommended for production)
    }

    /**
     * Exposes the AuthenticationManager bean, which is needed for authentication processes such as login.
     */
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean(); // Exposes the AuthenticationManager bean
    }

    /**
     * Configures the security for HTTP requests, defining which endpoints require authentication and which do not.
     * It also sets the session management policy to stateless since this is a REST API using JWT.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues()) // Enables CORS with default settings
                .and()
                .csrf().disable()  // Disables CSRF protection as this is a stateless REST API
                .authorizeRequests()
                .antMatchers("/user/login", "/user/signup", "/user/forgotPassword").permitAll() // Allows public access to login, signup, and forgot password endpoints
                .anyRequest().authenticated()  // Requires authentication for any other request
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);  // Configures stateless session management (no sessions, as JWT is used)

        // Adds the JwtFilter to validate JWT tokens before requests are processed by the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
