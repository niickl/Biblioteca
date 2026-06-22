package com.example.Biblioteca.repository;

import com.example.Biblioteca.Entity.UsuarioEntity;
import com.example.Biblioteca.dto.UsuarioResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;


import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, UUID> {

    Optional<UsuarioEntity> findByLogin(String login);

    @Query("""
        select new com.example.Biblioteca.dto.UsuarioResponseDTO(
            l.id,
            l.nome,
            l.login

        )from UsuarioEntity l 
        where lower(l.login) = lower(:login) 
""")
    Optional<UsuarioResponseDTO> BuscarPorLogin(@Param("login") String login);

    String login(String login);
}
