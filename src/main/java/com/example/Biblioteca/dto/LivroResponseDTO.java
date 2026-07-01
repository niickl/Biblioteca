package com.example.Biblioteca.dto;

public record LivroResponseDTO(
        Long id,
        String nome,
        String descricao,

        String nomeAutor,

        String nomeEditora,
		boolean lido
) {
}
