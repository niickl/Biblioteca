package com.example.Biblioteca.service;

import com.example.Biblioteca.Entity.RoleEntity;
import com.example.Biblioteca.Entity.UsuarioEntity;
import com.example.Biblioteca.dto.UsuarioRequestDTO;
import com.example.Biblioteca.dto.UsuarioResponseDTO;
import com.example.Biblioteca.repository.RoleRepository;
import com.example.Biblioteca.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;



    public UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO usuarioRequestDTO) {

        //verifica se já existe
        if (usuarioRepository.findByLogin(usuarioRequestDTO.login()).isPresent()) {
            throw new IllegalArgumentException("Usuário já existe");
        }

        // 2. Busca as roles no banco de dados baseadas nos Enums que vieram do DTO
        Set<RoleEntity> roles = usuarioRequestDTO.roles().stream()
                .map(roleEnum -> roleRepository.buscarPorStatus(roleEnum)
                        .orElseThrow(() -> new EntityNotFoundException("Role não encontrada: " + roleEnum)))
                .collect(Collectors.toSet());

        //monta a entidade
        UsuarioEntity usuario = new UsuarioEntity();
                                    usuario.setNome(usuarioRequestDTO.nome());
                                    usuario.setLogin(usuarioRequestDTO.login());
                                    //criptografa a senha
                                    usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.senha()));
                                    usuario.setRoles(roles);

        //Salva no banco
        UsuarioEntity usuarioSalvo = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getLogin()
        );
    }

    public UsuarioResponseDTO associarRole(UUID usuarioId, Long roleID) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        RoleEntity role = roleRepository.findById(roleID)
                .orElseThrow(() -> new EntityNotFoundException("Role não encontrada com ID: " + roleID));

        if (usuario.getRoles().contains(role)) {
            throw new IllegalArgumentException("O usuário já possui a role: " + role.getStatus());
        }

        usuario.getRoles().add(role);
        usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getLogin()
        );
    }

    public void removerRole (UUID usuarioId, Long roleID) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        RoleEntity role = roleRepository.findById(roleID)
                .orElseThrow(() -> new EntityNotFoundException("Role não encontrada com ID: " + roleID));

        usuario.getRoles().remove(role);
        usuarioRepository.save(usuario);
    }
}
