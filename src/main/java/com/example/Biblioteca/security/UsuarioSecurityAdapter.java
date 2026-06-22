package com.example.Biblioteca.security;

import com.example.Biblioteca.Entity.UsuarioEntity;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Uma classe separada apenas para o Spring Security ler
public class UsuarioSecurityAdapter implements UserDetails {

    private UsuarioEntity usuarioOriginal;

    public UsuarioSecurityAdapter(UsuarioEntity usuarioOriginal) {
        this.usuarioOriginal = usuarioOriginal;
    }

    //getter
    public UsuarioEntity getUsuarioOriginal() {
        return usuarioOriginal;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuarioOriginal.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getStatus().name().toUpperCase()))
                .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return usuarioOriginal.getSenha();
    }

    @Override
    public String getUsername() {
        return usuarioOriginal.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
