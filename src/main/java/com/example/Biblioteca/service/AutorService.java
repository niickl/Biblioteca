package com.example.Biblioteca.service;

import com.example.Biblioteca.Entity.AutorEntity;
import com.example.Biblioteca.dto.AutorRequestDTO;
import com.example.Biblioteca.dto.AutorResponseDTO;
import com.example.Biblioteca.repository.AutorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository autorRepository;

    @Transactional
    public AutorResponseDTO criar(AutorRequestDTO dto) {
        AutorEntity novoAutor = new AutorEntity();

        novoAutor.setNome(dto.nome());
        novoAutor.setIdade(dto.idade());
        novoAutor.setBiografia(dto.biografia());

        AutorEntity autorSalvo = autorRepository.save(novoAutor);

        return new AutorResponseDTO(
                autorSalvo.getId(),
                autorSalvo.getNome(),
                autorSalvo.getIdade(),
                autorSalvo.getBiografia()
        );
    }

    public List<AutorResponseDTO> listarTodos() {
        return autorRepository.findAll().stream()
                .map(autor -> new AutorResponseDTO(
                        autor.getId(),
                        autor.getNome(),
                        autor.getIdade(),
                        autor.getBiografia()
                ))
                .toList();
    }

    public List<AutorResponseDTO> buscarPorNome(String nome) {
        return autorRepository.findByNomeContainingIgnoreCase(nome);
    }

    public AutorResponseDTO buscarPorId(Long id) {
        AutorEntity autor = autorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor não encontrado com ID: " + id));
        return new AutorResponseDTO(
                autor.getId(),
                autor.getNome(),
                autor.getIdade(),
                autor.getBiografia()
        );
    }

    //atualizar
    public AutorResponseDTO atualizarPorID(Long id, AutorRequestDTO dto){
        //buscar o autor existente
        AutorEntity autor = autorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor não encontrado com id: " + id));

        //atualiza os dados
        autor.setNome(dto.nome());
        autor.setIdade(dto.idade());
        autor.setBiografia(dto.biografia());

        //salva DTO
        AutorEntity autorAtualizado = autorRepository.save(autor);

        return new AutorResponseDTO(
                autorAtualizado.getId(),
                autorAtualizado.getNome(),
                autorAtualizado.getIdade(),
                autorAtualizado.getBiografia()
        );
    }

    public AutorResponseDTO atualizarPorNome(String nome, AutorRequestDTO dto){

        AutorResponseDTO autorDTO = buscarPorNome(nome).stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Autor não encontrado com nome: " + nome));

        AutorEntity autor = autorRepository.findById(autorDTO.id())
                .orElseThrow(() -> new RuntimeException("Erro ao carregar entidade do autor"));

        AutorEntity autorAtualizado = autorRepository.save(autor);

        return new AutorResponseDTO(
                autorAtualizado.getId(),
                autorAtualizado.getNome(),
                autorAtualizado.getIdade(),
                autorAtualizado.getBiografia()
        );
    }

    //deletar

    public AutorResponseDTO deletarPorId(Long id) {
        AutorEntity autor = autorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor não encontrado com ID: " + id));
        autorRepository.delete(autor);
        return new AutorResponseDTO(
                autor.getId(),
                autor.getNome(),
                autor.getIdade(),
                autor.getBiografia()
        );
    }

    public AutorResponseDTO deletarPorNome(String nome){
        AutorResponseDTO autorEncontrado = buscarPorNome(nome).stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Autor não encontrado com nome: " + nome));
        autorRepository.deleteAllById(List.of(autorEncontrado.id()));
        return autorEncontrado;
    }

}
