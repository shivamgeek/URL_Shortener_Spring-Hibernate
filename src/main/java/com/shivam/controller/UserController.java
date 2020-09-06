package com.shivam.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shivam.Entity.URL;
import com.shivam.Entity.User;
import com.shivam.Service.UrlService;
import com.shivam.Service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;
	
	@Autowired
	UrlService urlService;
	
	@GetMapping("/goHome")
	public String goHome() {
		return "homepage";
	}
	
	
	@GetMapping("/register")
	public String registerUser(Model model) {
		User u = new User();
		model.addAttribute("userData", u);
		return "register-user";
	}
	
	
	@PostMapping("/saveCustomer")
	public String saveCustomerData(@ModelAttribute("userData") User user) {
		userService.saveUser(user);
		return "homepage";
	}
	
	
	@PostMapping("/login")
	public String login(Model model, @RequestParam("email") String email, @RequestParam("password") String password) {
		User user = userService.doLogin(email, password);
		if(user == null || email==null || password==null) {
			return "redirect:/user/goHome";
		}
		model.addAttribute("userData",user);
		return "user_homepage";	
	}
	
	
	@GetMapping("/createShortUrl/{userID}")
	public String createShortenedURL(Model model, @PathVariable("userID") int userID, @RequestParam("fullUrl") String originalUrl) {
		User user = userService.getUserFromId(userID);
		System.out.println("User is "+user);
		System.out.println("Full URL is "+originalUrl);
		URL url = new URL();
		url.setUser(user);
		url.setFullUrl(originalUrl);
		url.setExpirationDate("2020-9-06 05:50:01");
		user.addURL(url);
		
		//ADD URL shortening logic here
		url.setShortUrl(url.getFullUrl().hashCode()+"");
		
		urlService.saveUrl(url);
		model.addAttribute("userData",user);
		model.addAttribute("urlData",url);
		return "user_homepage";
	}
	
	@GetMapping("/showUrlList/{userID}")
	public String showUserUrlList(Model model, @PathVariable("userID") int userID) {
		User user = userService.getUserFromId(userID);
		List<URL> list = user.getUrlList();
		model.addAttribute("urlListData",list);
		model.addAttribute("userData", user);
		return "user_homepage";
	}
	
	@GetMapping("/deleteURL/{userId}/{urlId}")
	public String deleteURL(Model model, @PathVariable("userId") int userId, @PathVariable("urlId") int urlId) {
		urlService.removeUrl(urlId);
		User user = userService.getUserFromId(userId);
		List<URL> list = user.getUrlList();
		model.addAttribute("urlListData",list);
		model.addAttribute("userData",user);
		return "user_homepage";
	}
	
	
}
