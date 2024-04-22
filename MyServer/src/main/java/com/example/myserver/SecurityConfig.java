package com.example.myserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Collections;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserRepository userRepository;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()  // Assuming CSRF is disabled for testing purposes
			.authorizeRequests()
				.antMatchers("/users/register").permitAll()  // Allow everyone to access the register endpoint
				.anyRequest().authenticated()  // All other requests need authentication
			.and()
				.formLogin().permitAll()
			.and()
				.httpBasic();
	}


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsServiceBean())
            .passwordEncoder(passwordEncoder());
    }

    @Bean
    public UserDetailsService userDetailsServiceBean() {
        return username -> {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found.");
            }
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("USER")));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
