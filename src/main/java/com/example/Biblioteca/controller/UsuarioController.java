package com.example.Biblioteca.controller;

import com.example.Biblioteca.dto.UsuarioRequestDTO;
import com.example.Biblioteca.dto.UsuarioResponseDTO;
import com.example.Biblioteca.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PatchMapping("/{usuarioID}/roles/{roleId}")
    public ResponseEntity<UsuarioResponseDTO> associarRole(@PathVariable UUID usuarioID, @PathVariable Long roleId){
        UsuarioResponseDTO usuarioAtualizado = usuarioService.associarRole(usuarioID, roleId);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping("/{usuarioID}/roles/{roleId}")
    public ResponseEntity<Void> removerRole (@PathVariable UUID usuarioID, @PathVariable Long roleId) {
        usuarioService.removerRole(usuarioID, roleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO usuarioSalvo = usuarioService.registrarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
    }
}
