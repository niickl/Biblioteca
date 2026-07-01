package com.example.Biblioteca;

import com.example.Biblioteca.Entity.AutorEntity;
import com.example.Biblioteca.dto.AutorRequestDTO;
import com.example.Biblioteca.dto.AutorResponseDTO;
import com.example.Biblioteca.repository.AutorRepository;
import com.example.Biblioteca.service.AutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutorServiceTest {

    @Mock
    private AutorRepository autorRepository; //Duble do banco de dados

    @InjectMocks
    private AutorService autorService; //Classe real que será testada

    private AutorRequestDTO autorRequestDTO;

    private AutorEntity autorEntity;

    @BeforeEach
    void setUp (){
        autorRequestDTO = new AutorRequestDTO("Machado de Assis", 60, "Grande escritor");

        autorEntity = new AutorEntity();
        autorEntity.setId(1L);
        autorEntity.setNome("Machado de Assis");
        autorEntity.setIdade(60);
        autorEntity.setBiografia("Grande escritor");
    }

    @Test
    @DisplayName("Cenário 1: Deve criar um autor com sucesso")
    void deveCriarAutorComSucesso() {
		when(autorRepository.save(any(AutorEntity.class))).thenReturn(autorEntity);

        AutorResponseDTO autorCriado = autorService.criar(autorRequestDTO);

        assertNotNull(autorCriado);
        assertEquals(autorEntity.getId(), autorCriado.id());
        assertEquals(autorEntity.getNome(), autorCriado.nome());
        assertEquals(autorEntity.getIdade(), autorCriado.idade());
        assertEquals(autorEntity.getBiografia(), autorCriado.biografia());

        verify(autorRepository, times(1)).save(any(AutorEntity.class));
    }

    @Test
    @DisplayName("Cenário 2: Deve lançar IllegalArgumentException quando o nome for em branco")
    void deveLancarExcecaoQuandoNomeEmBranco() {
        AutorRequestDTO dtoInvalido = new AutorRequestDTO("", 60, "Biografia");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->{
            autorService.criar(dtoInvalido);
        });

        assertEquals("O nome do autor não pode estar em branco", exception.getMessage());

        verify(autorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cenário 3: Deve lançar RuntimeException ao buscar ID inexistente")
    void deveLancarExcecaoQuandoBuscarPorIdInexistente() {
        Long idInexistente = 99L;
        when(autorRepository.findById(idInexistente)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            autorService.buscarPorId(idInexistente);
        });

        assertEquals("Autor não encontrado com ID: " + idInexistente, exception.getMessage());

        verify(autorRepository, times(1)).findById(idInexistente);
    }


}
