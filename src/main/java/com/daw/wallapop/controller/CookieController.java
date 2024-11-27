package com.daw.wallapop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CookieController {

    @GetMapping("/set-cookie")
    public String setCookie(HttpServletResponse response, @RequestParam String cookieValue) {
        // Crear la cookie
        Cookie cookie = new Cookie("myCookie", cookieValue);

        // Establecer el tiempo de expiración de la cookie (5 días en segundos)
        cookie.setMaxAge(5 * 24 * 60 * 60); // 5 días en segundos

        // Hacer que la cookie esté disponible en todo el dominio (opcional)
        cookie.setPath("/");

        // Si quieres que la cookie solo se transmita a través de HTTPS (opcional)
        cookie.setSecure(true); // Solo si el sitio usa HTTPS

        // Agregar la cookie a la respuesta
        response.addCookie(cookie);

        // Retornar alguna vista o redirigir
        return "cookie-set"; // Este es solo un ejemplo, puedes cambiarlo según lo que necesites
    }

    @GetMapping("/get-cookie")
    public String getCookie(@RequestParam(value = "myCookie", defaultValue = "No cookie found") String cookieValue) {
        // Lógica para obtener la cookie y procesarla
        return "cookie-value: " + cookieValue;
    }
}
