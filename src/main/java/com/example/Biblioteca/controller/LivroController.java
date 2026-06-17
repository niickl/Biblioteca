package com.example.Biblioteca.controller;

import com.example.Biblioteca.service.LivroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/livros")
public class LivroController {

    private final LivroService livroService;

    @PostMapping
    public ResponseEntity<LivroResponseDTO> criarLivro(@RequestBody LivroRequestDTO dto) throws Throwable {
        LivroResponseDTO livroSalvo = livroService.criar(dto);

        return ResponseEntity.ok(livroSalvo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> obterLivroPorId(@PathVariable Long id){
        LivroResponseDTO livro = livroService.buscarPorId(id);
        return ResponseEntity.ok(livro);
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<LivroResponseDTO>> obterLivroPorNome(@PathVariable String nome) {
        List<LivroResponseDTO> livros = livroService.buscarPorNome(nome);
        return ResponseEntity.ok(livros);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> deletarLivroPorId(@PathVariable Long id) {
        LivroResponseDTO livro = livroService.deletarPorId(id);
        return ResponseEntity.ok(livro);
    }

    @DeleteMapping("/nome/{nome}")
    public ResponseEntity<LivroResponseDTO> deletarLivroPorNome(@PathVariable String nome) {
        LivroResponseDTO livro = livroService.deletarPorNome(nome);
        return ResponseEntity.ok(livro);
    }

}
