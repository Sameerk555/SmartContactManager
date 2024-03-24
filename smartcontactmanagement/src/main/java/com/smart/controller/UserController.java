package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;




@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	//method for adding common data
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String username=principal.getName();
		System.out.println("username " +username);
		// get user by username
		User user=userRepository.getUserByUserName(username);
		
		System.out.println("user " +user);
		
		model.addAttribute("user", user);
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		
		return "normal/user_dashboard";
	}
	
	//add form handler
	@GetMapping("/add-contact")
	public String addContactForm(Model m) {
		m.addAttribute("title", "Add Contact");
		m.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,@RequestParam("profileImage")MultipartFile file, Principal principal, HttpSession session) {
		try {
		   String name= principal.getName();
		   User user=this.userRepository.getUserByUserName(name);
		   
	
		   //processing and uploading file
		   if(file.isEmpty()) {
			   System.out.println("file is empty");
			   contact.setImage("contact.png");
		   }
		   else {
			   contact.setImage(file.getOriginalFilename());
			   
			     
			   File saveFile=new ClassPathResource("static/images").getFile();
			   Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			   Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		   }
		   contact.setUser(user);
		   user.getContacts().add(contact);
		   this.userRepository.save(user);
		   System.out.println("Added to database");
		   System.out.println("Data "+contact);
		   // message success.........
//		   session.setAttribute("message", new Message("Your contact is added ! add more", "success"));
		   return "normal/add_contact_form";
		}catch(Exception e) {
			System.out.println("error "+e.getMessage());
//			e.printStackTrace();
			session.setAttribute("message", new Message("Your contact is not added ! something went wrong", "danger"));
			return "normal/add_contact_form";
		}
		  
	}      
	
	// show contact handler
	// per page 5 contacts
	//current page=0 [page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page")Integer page ,Model m, Principal principal) {
		m.addAttribute("title", "show contacts");
//		String userName= principal.getName();
//		User user=  this.userRepository.getUserByUserName(userName);
//		List<Contact>contacts= user.getContacts()
		String userName= principal.getName();
		User user= this.userRepository.getUserByUserName(userName);
		Pageable pageable= PageRequest.of(page, 5);
		Page<Contact> contacts=this.contactRepository.findContactByUser(user.getId(), pageable);
		m.addAttribute("contacts", contacts); 
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_contacts";
	}
	  
	//showing particular contact details     
	@GetMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId")Integer cId, Model m, Principal principal) {
//		System.out.println("contact id "+ cId);
		Optional<Contact>contactOptional=this.contactRepository.findById(cId);
		Contact contact =contactOptional.get();
		
		//
		String userName=principal.getName();
		User user= this.userRepository.getUserByUserName(userName);
		if(user.getId()==contact.getUser().getId()) {
			m.addAttribute("model", contact);
			m.addAttribute("title", contact.getName());
		}
		return "normal/contact_details";
	}
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId")Integer cId, Model m, HttpSession session, Principal principal) {
//		Optional<Contact>contactOptional= this.contactRepository.findById(cId);
		Contact contact= this.contactRepository.findById(cId).get();
		System.out.println("Contact "+cId);
//		contact.setUser(null);
//		this.contactRepository.delete(contact);  
//		session.setAttribute("message", new Message("contact deleted successfully", "success"));
		User user= this.userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		this.userRepository.save(user);
		return "redirect:/user/show-contacts/0";
	}
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId")Integer cId, Model m) {
		m.addAttribute("title", "update contact");   
		Contact contact =this.contactRepository.findById(cId).get();
		m.addAttribute("contact",contact);
		return "normal/update_form";
	}
	//update contact handler
	@PostMapping("/process-update")
	public String processUpdate(@ModelAttribute Contact contact, @RequestParam("profileImage")MultipartFile file, Model m, HttpSession session, Principal principal) {
		try {
			
			//old contact details
			Contact oldContactDetail=this.contactRepository.findById(contact.getcId()).get();
			if(!file.isEmpty()) {
				//delete old photo
				File deleteFile=new ClassPathResource("static/images").getFile();
				File file1= new File(deleteFile, oldContactDetail.getImage());
				file1.delete();
				//update a photo
				 File saveFile=new ClassPathResource("static/images").getFile();
				 Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				 Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				 contact.setImage(file.getOriginalFilename());
				
			}
			else {
				contact.setImage(oldContactDetail.getImage());
			}
			User user= this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
		}catch(Exception e) {
			
		}
		System.out.println("cotact name " +contact.getName());
		System.out.println("Contact id " +contact.getcId());
		
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	// your profile handler
	@GetMapping("/profile")
	public String userProfile(Model m) {
		
		m.addAttribute("title", "profile page");
		
		return "normal/profile";
	}
}
