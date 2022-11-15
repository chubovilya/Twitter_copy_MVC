package com.chubov.twetter_clone.controller;

import com.chubov.twetter_clone.domain.User;
import com.chubov.twetter_clone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@Valid User user, BindingResult bindingResult, Model model) {
        //Password validation/catching error
        if (user.getPassword()!=null && !user.getPassword().equals(user.getPassword_confirmation())){
            model.addAttribute("passwordError","Passwords not equal");
            return "registration";
        }

        if (bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);

            model.mergeAttributes(errors);
            return "registration";
        }


        /*Если пользователь найден в БД, то вывести User exist,
            иначе добавляем пользователя и редиректим на login page*/
        if (!userService.addUser(user)) {
            model.addAttribute("usernameError", "User already exists");
            return "registration";
        }

        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        boolean isActivated = userService.activateUser(code);

        if (isActivated){
            model.addAttribute("message", "User successfully activated");
        }else {
            model.addAttribute("message", "Activation code is not found.");
        }

        return "login";
    }
}
