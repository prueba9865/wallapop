package com.daw.wallapop.config;

import com.daw.wallapop.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration      //Especifica que es una clase de configuración
@EnableWebSecurity  //Especificamos con esta clase vamos a configurar la seguridad de la App
public class SecurityConfig {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/", "/register", "/anuncios/ver/**", "/imagesAnuncios/**", "/anuncios/fotos/delete/*").permitAll()
                                .requestMatchers("/anuncios/new/**").authenticated()
                                .requestMatchers("/anuncios/edit/**").authenticated()
                                .requestMatchers("/anuncios/del/**").authenticated()
                                .requestMatchers("/misanuncios/**").authenticated()
                )
                .formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/")
                                .failureUrl("/login?error=true")
                                .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para el logout
                        .logoutSuccessUrl("/login") // URL de redirección después de logout
                        .permitAll()
                ) ;


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(customUserDetailsService);
        return provider;
    }
}