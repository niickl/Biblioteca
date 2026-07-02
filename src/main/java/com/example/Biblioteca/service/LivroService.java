package com.example.Biblioteca.service;

import com.example.Biblioteca.Entity.AutorEntity;
import com.example.Biblioteca.Entity.EditoraEntity;
import com.example.Biblioteca.Entity.LivroEntity;
import com.example.Biblioteca.dto.LivroRequestDTO;
import com.example.Biblioteca.dto.LivroResponseDTO;
import com.example.Biblioteca.repository.AutorRepository;
import com.example.Biblioteca.repository.EditoraRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.Biblioteca.repository.LivroRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;
    private final EditoraRepository editoraRepository;


    @Transactional
    public LivroResponseDTO criar (LivroRequestDTO dto){

        AutorEntity autor = autorRepository.findById(dto.autorId())
                .orElseThrow(() -> new EntityNotFoundException("Autor não encontrado com o ID: " + dto.autorId()));

        EditoraEntity editora = editoraRepository.findById(dto.editoraId())
                .orElseThrow(() -> new EntityNotFoundException("Editora não encontrada com o ID: " + dto.editoraId()));

        LivroEntity novoLivro = new LivroEntity();
        novoLivro.setNome(dto.nome());
        novoLivro.setDescricao(dto.descricao());
        novoLivro.setAutor(autor);
        novoLivro.setEditora(editora);
        novoLivro.setLido(false);

        LivroEntity livroSalvo = livroRepository.save(novoLivro);
        return new LivroResponseDTO(
                livroSalvo.getId(),
                livroSalvo.getNome(),
                livroSalvo.getDescricao(),
                livroSalvo.getAutor().getNome(),
                livroSalvo.getEditora().getNome(),
                livroSalvo.getLido()
        );

    }

    //lista todos os livros
    public List<LivroResponseDTO> listarTodos() {
        return livroRepository.findAll().stream()
                .map(livro -> new LivroResponseDTO(
                        livro.getId(),
                        livro.getNome(),
                        livro.getDescricao(),
                        livro.getAutor().getNome(),
                        livro.getEditora().getNome(),
                        livro.getLido()
                ))
                .toList();
    }

    //buscar livro por id
    public LivroResponseDTO buscarPorId(Long id) {
        LivroEntity livro = livroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado com o ID: " + id));

        return new LivroResponseDTO(
                livro.getId(),
                livro.getNome(),
                livro.getDescricao(),
                livro.getAutor().getNome(),
                livro.getEditora().getNome(),
                livro.getLido()
        );
    }

    public List<LivroResponseDTO> buscarPorNome(String nome) {
        return livroRepository.findByNomeContainingIgnoreCase(nome);
    }

    //deletar livro
    public LivroResponseDTO deletarPorId(Long id) {
        LivroEntity livro = livroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado com o ID: " + id));
        livroRepository.delete(livro);
        return new LivroResponseDTO(
                livro.getId(),
                livro.getNome(),
                livro.getDescricao(),
                livro.getAutor().getNome(),
                livro.getEditora().getNome(),
                livro.getLido()
        );
    }

    public LivroResponseDTO deletarPorNome(String nome) {
        LivroResponseDTO livroEncontrado = buscarPorNome(nome).stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado com nome: " + nome));
        livroRepository.deleteAllById(List.of(livroEncontrado.id()));
        return livroEncontrado;
    }

    public LivroResponseDTO marcarComoLido (Long id) {

        LivroEntity livro = livroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado com o ID: " + id));

        livro.setLido(true);
        livroRepository.save(livro);

        return new LivroResponseDTO(
                livro.getId(),
                livro.getNome(),
                livro.getDescricao(),
                livro.getAutor().getNome(),
                livro.getEditora().getNome(),
                livro.getLido()
        );
    }

}
