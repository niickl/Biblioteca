package com.example.Biblioteca.repository;

import com.example.Biblioteca.Entity.RoleEntity;
import com.example.Biblioteca.Entity.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    @Query("""
        select r from RoleEntity r 
        where r.status = :status
""")
    Optional<RoleEntity> buscarPorStatus(@Param("status") RoleEnum status);
}
