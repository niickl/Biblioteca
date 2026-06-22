package com.example.Biblioteca.dto;

import com.example.Biblioteca.Entity.RoleEnum;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record UsuarioRequestDTO (

        @NotBlank String nome,
        @NotBlank String login,
        @NotBlank String senha,
        Set<RoleEnum> roles
) { }
