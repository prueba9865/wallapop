package com.daw.wallapop.service;

import com.daw.wallapop.entity.Usuario;
import com.daw.wallapop.repository.UsuarioRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public void añadirUsuario(Usuario usuario){
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
    }
    public UserDetails getUserAuth(){
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    public String añadirCookieEncrypted(Authentication authentication, Model model, HttpServletResponse response){
        // Verificar si el usuario está autenticado
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            // Aquí puedes acceder al usuario autenticado
            Usuario usuario = (Usuario) this.getUserAuth();

            model.addAttribute("usuario", usuario);

            String encryptedUsername = this.cifrarUsuario();

            this.añadirCookie(encryptedUsername, response);

            // Redirigir a la página principal o al dashboard
            return "login";  // O la vista que necesites
        }
        return "login";
    }

    public void añadirCookie(String encryptedUsername, HttpServletResponse response){
        Cookie cookie = new Cookie("COOKIE", encryptedUsername);  // Establecer la cookie
        cookie.setMaxAge(5 * 24 * 60 * 60);  // 5 días de duración (en segundos)
        cookie.setPath("/");  // La cookie estará disponible en t0do el dominio

        // Si estás usando HTTPS, es recomendable que establezcas 'secure'
        cookie.setSecure(true);  // Solo si usas HTTPS

        // Agregar la cookie a la respuesta
        response.addCookie(cookie);
    }

    public String cifrarUsuario(){
        // Cifrar el nombre de usuario antes de guardarlo en la cookie
        String encryptedUsername = null;
        try {
            encryptedUsername = CifradoService.encrypt(this.getUserAuth().getUsername());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptedUsername;
    }
}
