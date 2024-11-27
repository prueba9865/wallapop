package com.daw.wallapop.controller;

import com.daw.wallapop.entity.Usuario;
import com.daw.wallapop.service.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
class LoginController {

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/login")
    public String login(Model model, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return usuarioService.añadirCookieEncrypted(authentication, model, response);
    }


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    @PostMapping("/register")
    public String register(Model model, @Valid @ModelAttribute("usuario") Usuario usuario, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        usuarioService.añadirUsuario(usuario);
        return "redirect:/login";
    }
}
