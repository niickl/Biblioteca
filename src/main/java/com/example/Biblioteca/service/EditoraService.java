package com.example.Biblioteca.service;

import com.example.Biblioteca.Entity.AutorEntity;
import com.example.Biblioteca.Entity.EditoraEntity;
import com.example.Biblioteca.Entity.LivroEntity;
import com.example.Biblioteca.dto.EditoraRequestDTO;
import com.example.Biblioteca.dto.EditoraResponseDTO;
import com.example.Biblioteca.dto.LivroResponseDTO;
import com.example.Biblioteca.repository.EditoraRepository;
import com.example.Biblioteca.repository.LivroRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EditoraService {

    private final EditoraRepository editoraRepository;

    @Transactional
    public EditoraResponseDTO criar(EditoraRequestDTO dto) {

        EditoraEntity novaEditora = new EditoraEntity();
        novaEditora.setNome(dto.nome());
        novaEditora.setCnpj(dto.cnpj());

        EditoraEntity editoraSalva = editoraRepository.save(novaEditora);
        return new EditoraResponseDTO(
                editoraSalva.getId(),
                editoraSalva.getNome(),
                editoraSalva.getCnpj()
        );

    }

    //buscar
    public List<EditoraResponseDTO> buscarPorNome(String nome) {
        return editoraRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<EditoraResponseDTO> listarTodos(){
         return editoraRepository.findAll().stream()
                .map(editora -> new EditoraResponseDTO(
                        editora.getId(),
                        editora.getNome(),
                        editora.getCnpj()
                ))
                .toList();
    }

    public EditoraResponseDTO buscarPorId(Long id){
        EditoraEntity editora = editoraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Editora não encontrada com id: " + id));
        return new EditoraResponseDTO(
                editora.getId(),
                editora.getNome(),
                editora.getCnpj()
        );
    }

    //delete
    public EditoraResponseDTO deletarPorId(Long id) {
         EditoraEntity editora = editoraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Editora não encontrada com id: " + id));
        editoraRepository.delete(editora);
        return new EditoraResponseDTO(
                editora.getId(),
                editora.getNome(),
                editora.getCnpj()
        );
    }

    public EditoraResponseDTO deletarPorNome(String nome){
         EditoraResponseDTO editoraEncontrada = buscarPorNome(nome).stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Editora não encontrada com nome: " + nome));
        editoraRepository.deleteById(editoraEncontrada.id());
        return editoraEncontrada;
    }


}
