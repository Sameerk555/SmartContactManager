package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;   
	@Autowired
	private UserRepository userRepository;
	 
	@GetMapping("/")    
	public String home(Model m) {
		
		m.addAttribute("title", "Home-this is smart contact manager");
		
		return "home";    
	}  
	@GetMapping("/signup")
	public String signup(Model m) {
		
		m.addAttribute("title", "Register-Smart Contact Manager");
		m.addAttribute("user", new User());
		return "signup";    
	}
	// this handler for registering user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1, @RequestParam(value="agreement", 
	                     defaultValue="false") boolean agreement, Model m ,HttpSession session) {
		
		try {	
			if(!agreement) {
				System.out.println("you have not agreed the terms and conditions");
				throw new Exception("you have not agreed the terms and conditions");
			}
			if(result1.hasErrors()) {
				System.out.println("Error "+result1.toString());
				m.addAttribute("user", user);
//				throw new Exception("error occurred");
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("agreement "+agreement);
			System.out.println("user "+user);  
			User result=this.userRepository.save(user);
			m.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully registered", "alert-success"));
			return "signup";
		}catch(Exception e) {
			e.printStackTrace();
			m.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong"+e.getMessage(), "alert-danger"));
			return "signup";
		}
		
		
	}
	//handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "Login page");
		return "login";
	}
}
   