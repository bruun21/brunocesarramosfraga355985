package com.bruno.artistalbum.dto;

import com.bruno.artistalbum.model.Album;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumDTO {
    private UUID id;
    private String titulo;
    private Integer anoLancamento;
    private List<String> urlsImagens;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public AlbumDTO(Album album) {
        this.id = album.getId();
        this.titulo = album.getTitulo();
        this.anoLancamento = album.getAnoLancamento();
        this.urlsImagens = album.getUrlsImagens();
        this.criadoEm = album.getCriadoEm();
        this.atualizadoEm = album.getAtualizadoEm();
    }
}
