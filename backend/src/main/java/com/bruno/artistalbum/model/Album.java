package com.bruno.artistalbum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "albuns")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(name = "ano_lancamento")
    private Integer anoLancamento;

    @ManyToMany(mappedBy = "albuns", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Artista> istas = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "album_imagens", joinColumns = @JoinColumn(name = "album_id"))
    @Column(name = "url_imagem")
    private List<String> urlsImagens = new ArrayList<>();

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    @PreUpdate
    protected void aoAtualizar() {
        atualizadoEm = LocalDateTime.now();
    }
}
