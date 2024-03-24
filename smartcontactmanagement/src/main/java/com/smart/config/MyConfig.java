package com.smart.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@EnableWebSecurity
@Configuration
//@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class MyConfig {

    @Bean
    public UserDetailsService getUserDetailService() {    

        return new UserDetailsServiceImpl();
    }
    //extends WebSecurityConfiguration   
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

         return new BCryptPasswordEncoder();   
    }    

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
  
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }
    @Bean
 	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	http.csrf().disable().authorizeHttpRequests().requestMatchers("/admin/**").hasRole("ADMIN")
		.requestMatchers("/user/**").hasRole("USER")
		.requestMatchers("/**").permitAll().and().formLogin().loginPage("/signin")
		.loginProcessingUrl("/dologin").defaultSuccessUrl("/user/index").failureUrl("/login-fail").and().csrf().disable();
 		return http.build();
 	}

	
	// configure method..
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
//		auth.authenticationProvider(authenticationProvider());
//	}
//    protected void configure(HttpSecurity http) throws Exception{
//    	http.authorizeHttpRequests().requestMatchers("/admin/**").hasRole("ADMIN")
//		.requestMatchers("/user/**").hasRole("USER")
//		.requestMatchers("/**").permitAll().and().formLogin().and().csrf().disable();
//	}
	
    
    
}