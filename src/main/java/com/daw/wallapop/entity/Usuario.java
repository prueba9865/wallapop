package com.daw.wallapop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "usuarios")
@Entity
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "{email.email}")
    @Column(unique = true, nullable = false)
    private String email;

    @Size(min = 4, message = "{password.size}")
    @Column(length = 500)
    private String password;

    private String nombre;
    private String telefono;
    private String poblacion;

    @OneToMany(targetEntity = Anuncio.class, cascade = CascadeType.ALL,
            mappedBy = "usuario")
    private List<Anuncio> anuncios = new ArrayList<>();

    // Constructor si lo necesitas (si no usas Lombok)
    public Usuario(Long id, String email, String password, String nombre, String telefono, String poblacion) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.telefono = telefono;
        this.poblacion = poblacion;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Aquí deberías devolver los roles del usuario (por ejemplo, "ROLE_USER")
        // Si no tienes roles, puedes devolver al menos un rol por defecto
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return this.email; // El email o el nombre de usuario
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Si no gestionas la expiración, lo dejas en true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Aquí podrías poner la lógica para verificar si la cuenta está bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Verifica si las credenciales no han expirado
    }

    @Override
    public boolean isEnabled() {
        return true; // Verifica si la cuenta está habilitada
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", poblacion='" + poblacion + '\'' +
                '}';
    }
}
