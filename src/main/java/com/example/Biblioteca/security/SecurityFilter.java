package com.example.Biblioteca.security;

import com.example.Biblioteca.Entity.UsuarioEntity;
import com.example.Biblioteca.repository.UsuarioRepository;
import com.example.Biblioteca.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        var token = this.recoverToken(request);
            if (token != null) {

                var login = tokenService.validateToken(token);

                if (!login.isEmpty()) {
                UsuarioEntity usuario = usuarioRepository.findByLogin(login)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + login));

                List<SimpleGrantedAuthority> permissoes = usuario.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getStatus().name().toUpperCase()))
                        .toList();

                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, permissoes);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request,response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null )
            return null;
        return authHeader.replace("Bearer ", "");
    }

}
