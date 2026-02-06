package com.bruno.artistalbum.dto;

import com.bruno.artistalbum.model.Album;
import com.bruno.artistalbum.service.MinioService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumDTO {
    private UUID id;
    private String titulo;
    private Integer anoLancamento;
    private List<String> urlsImagens;
    private List<UUID> artistaIds;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    /**
     * Construtor que converte Album para DTO sem gerar URLs presigned.
     * As URLs serão os nomes dos arquivos armazenados no banco.
     */
    public AlbumDTO(Album album) {
        this.id = album.getId();
        this.titulo = album.getTitulo();
        this.anoLancamento = album.getAnoLancamento();
        this.urlsImagens = album.getUrlsImagens() != null ? new ArrayList<>(album.getUrlsImagens()) : null;
        this.artistaIds = album.getArtistas() != null ? album.getArtistas().stream()
                .map(com.bruno.artistalbum.model.Artista::getId).collect(Collectors.toList()) : null;
        this.criadoEm = album.getCriadoEm();
        this.atualizadoEm = album.getAtualizadoEm();
    }

    /**
     * Construtor que converte Album para DTO gerando URLs pré-assinadas.
     * As URLs terão expiração de 30 minutos.
     */
    public AlbumDTO(Album album, MinioService minioService) {
        this.id = album.getId();
        this.titulo = album.getTitulo();
        this.anoLancamento = album.getAnoLancamento();
        this.criadoEm = album.getCriadoEm();
        this.atualizadoEm = album.getAtualizadoEm();
        this.artistaIds = album.getArtistas() != null ? album.getArtistas().stream()
                .map(com.bruno.artistalbum.model.Artista::getId).collect(Collectors.toList()) : null;

        // Gera URLs pré-assinadas para cada arquivo
        if (album.getUrlsImagens() != null && !album.getUrlsImagens().isEmpty()) {
            this.urlsImagens = album.getUrlsImagens().stream()
                    .map(minioService::getPresignedUrl)
                    .collect(Collectors.toList());
        } else {
            this.urlsImagens = null;
        }
    }
}
