package com.bruno.artistalbum.dto;

import com.bruno.artistalbum.model.Artista;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistaDTO {
    private UUID id;
    private String nome;
    private com.bruno.artistalbum.model.TipoArtista tipo;
    private LocalDateTime criadoEm;

    public ArtistaDTO(Artista artista) {
        this.id = artista.getId();
        this.nome = artista.getNome();
        this.tipo = artista.getTipo();
        this.criadoEm = artista.getCriadoEm();
    }
}
