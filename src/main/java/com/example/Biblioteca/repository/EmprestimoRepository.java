package com.example.Biblioteca.repository;

import com.example.Biblioteca.Entity.EmprestimoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<EmprestimoEntity, Long> {

    @Query("""
        SELECT e FROM EmprestimoEntity e 
        JOIN FETCH e.usuario JOIN FETCH e.livro
        WHERE e.devolvido = false AND e.dataDevolucaoPrevista = :data
            """)
    List<EmprestimoEntity> buscarEmprestimosQueVencemEm(@Param("data") java.time.LocalDate data);
}
