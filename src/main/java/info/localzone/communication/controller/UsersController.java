package info.localzone.communication.controller;

import info.localzone.communication.model.security.User;
import info.localzone.communication.model.security.UserCreateForm;
import info.localzone.communication.service.security.UserService;

import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class UsersController {
	
	UserService userService;
	UserCreateFormValidator userCreateFormValidator;

    @Autowired
    public UsersController(UserService userService, UserCreateFormValidator userCreateFormValidator) {
        this.userService = userService;
        this.userCreateFormValidator = userCreateFormValidator;
    }

	@RequestMapping("/users")
	public ModelAndView getUsersPage() {
		return new ModelAndView("users", "users", userService.getAllUsers());
	}

	@PreAuthorize("@currentUserServiceImpl.canAccessUser(principal, #id)")
	@RequestMapping("/user/{id}")
	public ModelAndView getUserPage(@PathVariable Long id) {
		User user = userService.getUserById(id);
		if (user == null)
			new NoSuchElementException(String.format("User=%s not found", id));
		return new ModelAndView("user", "user", user);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/user/create", method = RequestMethod.GET)
	public ModelAndView getUserCreatePage() {
		return new ModelAndView("userCreate", "form", new UserCreateForm());
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/user/create", method = RequestMethod.POST)
	public String handleUserCreateForm(@Valid @ModelAttribute("form") UserCreateForm form, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "user_create";
		}
		try {
			userService.create(form);
		} catch (DataIntegrityViolationException e) {
			bindingResult.reject("email.exists", "Email already exists");
			return "user_create";
		}
		return "redirect:/users";
	}
}

