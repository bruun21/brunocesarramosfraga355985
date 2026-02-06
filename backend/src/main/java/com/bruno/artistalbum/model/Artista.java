package com.bruno.artistalbum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "artistas")
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoArtista tipo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "artista_album", joinColumns = @JoinColumn(name = "artista_id"), inverseJoinColumns = @JoinColumn(name = "album_id"))
    @lombok.EqualsAndHashCode.Exclude
    @lombok.ToString.Exclude
    private Set<Album> albuns = new HashSet<>();

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
}
