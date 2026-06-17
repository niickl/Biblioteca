package com.example.Biblioteca.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table (name = "LIVRO")
public class LivroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "descricao", length = Integer.MAX_VALUE)
    private String descricao;

    @Size(max = 255)
    @Column(name = "nome")
    private String nome;

    @ColumnDefault("false")
    @Column(name = "lido")
    private Boolean lido;

    @ColumnDefault("NULL")
    @Column(name = "excluido_em")
    private Instant excluidoEm;

    @CreationTimestamp
    @Column(name = "criado_em")
    private Instant criadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "editora_id")
    private EditoraEntity editora;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "autor_id")
    private AutorEntity autor;

}
