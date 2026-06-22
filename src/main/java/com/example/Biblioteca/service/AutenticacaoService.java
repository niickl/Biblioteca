package com.example.Biblioteca.service;

import com.example.Biblioteca.Entity.UsuarioEntity;
import com.example.Biblioteca.repository.UsuarioRepository;
import com.example.Biblioteca.security.UsuarioSecurityAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService implements UserDetailsService {
    @Autowired
    private UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioEntity usuario = repository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
        // Usa o repository que agora retorna um Optional.
        return new UsuarioSecurityAdapter(usuario);
    }
}
