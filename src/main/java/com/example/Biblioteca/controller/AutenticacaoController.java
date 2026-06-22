package com.example.Biblioteca.controller;

import com.example.Biblioteca.Entity.UsuarioEntity;
import com.example.Biblioteca.dto.LoginRequestDTO;
import com.example.Biblioteca.dto.TokenResponseDTO;
import com.example.Biblioteca.security.UsuarioSecurityAdapter;
import com.example.Biblioteca.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.login(), dto.senha());
        var auth = authenticationManager.authenticate(usernamePassword);

        var adaptador = (UsuarioSecurityAdapter) auth.getPrincipal();

        var token = tokenService.generateToken(adaptador.getUsuarioOriginal());

        return ResponseEntity.ok(new TokenResponseDTO(token));
    }

}
