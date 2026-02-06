package com.bruno.artistalbum.service;

import com.bruno.artistalbum.dto.AlbumDTO;
import com.bruno.artistalbum.model.Album;
import com.bruno.artistalbum.repository.AlbumRepository;
import com.bruno.artistalbum.exception.RegraDeNegocioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.io.IOException;
import java.time.Instant;

import java.util.UUID;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private com.bruno.artistalbum.repository.ArtistaRepository artistaRepository;

    @Autowired
    private MinioService minioService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    public Page<AlbumDTO> listarTodos(Pageable paginacao) {
        return albumRepository.findAll(paginacao)
                .map(album -> new AlbumDTO(album, minioService));
    }

    @Transactional(readOnly = true)
    public Page<AlbumDTO> buscarPorTipoArtista(com.bruno.artistalbum.model.TipoArtista tipo, Pageable paginacao) {
        return albumRepository.findByArtistasTipo(tipo, paginacao)
                .map(album -> new AlbumDTO(album, minioService));
    }

    @Transactional(readOnly = true)
    public AlbumDTO buscarPorId(UUID id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Album não encontrado"));
        return new AlbumDTO(album, minioService);
    }

    @Transactional
    public AlbumDTO salvar(AlbumDTO dadosAlbum) {
        Album album = new Album();
        album.setTitulo(dadosAlbum.getTitulo());
        album.setAnoLancamento(dadosAlbum.getAnoLancamento());
        album.setUrlsImagens(dadosAlbum.getUrlsImagens());

        if (dadosAlbum.getArtistaIds() != null && !dadosAlbum.getArtistaIds().isEmpty()) {
            java.util.List<com.bruno.artistalbum.model.Artista> artistas = artistaRepository
                    .findAllById(dadosAlbum.getArtistaIds());
            album.setArtistas(new java.util.HashSet<>(artistas));
            // Sincroniza o lado inverso para JPA
            final Album albumFinal = album;
            artistas.forEach(a -> a.getAlbuns().add(albumFinal));
        }

        album = albumRepository.save(album);

        AlbumDTO dto = new AlbumDTO(album);
        messagingTemplate.convertAndSend("/topic/albuns/novos", dto);

        return dto;
    }

    @Transactional
    public AlbumDTO atualizar(UUID id, AlbumDTO dadosAlbum) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Album não encontrado"));
        album.setTitulo(dadosAlbum.getTitulo());
        album.setAnoLancamento(dadosAlbum.getAnoLancamento());
        album.setUrlsImagens(dadosAlbum.getUrlsImagens());

        if (dadosAlbum.getArtistaIds() != null) {
            // Limpa associações antigas
            final Album albumParaRemover = album;
            album.getArtistas().forEach(a -> a.getAlbuns().remove(albumParaRemover));

            java.util.List<com.bruno.artistalbum.model.Artista> artistas = artistaRepository
                    .findAllById(dadosAlbum.getArtistaIds());
            album.setArtistas(new java.util.HashSet<>(artistas));
            // Re-associa
            final Album albumParaAdicionar = album;
            artistas.forEach(a -> a.getAlbuns().add(albumParaAdicionar));
        }

        album = albumRepository.save(album);
        return new AlbumDTO(album);
    }

    @Transactional
    public void deletar(UUID id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Album não encontrado"));

        // Remove imagens do MinIO
        if (album.getUrlsImagens() != null) {
            album.getUrlsImagens().forEach(minioService::deleteFile);
        }

        albumRepository.delete(album);
    }

    @Transactional
    public AlbumDTO adicionarImagem(UUID id, MultipartFile imagem) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Album não encontrado"));

        String filename = "album-" + id + "-" + Instant.now().toEpochMilli() + "-" + imagem.getOriginalFilename();

        try {
            // uploadFile agora retorna apenas o nome do arquivo
            String nomeArquivo = minioService.uploadFile(filename, imagem.getInputStream(), imagem.getContentType());

            if (album.getUrlsImagens() == null) {
                album.setUrlsImagens(new java.util.ArrayList<>());
            }
            // Salva apenas o nome do arquivo no banco
            album.getUrlsImagens().add(nomeArquivo);

            album = albumRepository.save(album);
            // Gera DTO com URLs presigned para retornar ao cliente
            AlbumDTO dto = new AlbumDTO(album, minioService);

            // Notifica via WebSocket
            messagingTemplate.convertAndSend("/topic/albuns/" + id, dto);

            return dto;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar imagem", e);
        }
    }

    @Transactional
    public com.bruno.artistalbum.dto.PresignedUploadDTO obterUrlUpload(UUID id, String extensao) {
        if (!albumRepository.existsById(id)) {
            throw new RegraDeNegocioException("Album não encontrado");
        }

        String filename = "album-" + id + "-" + Instant.now().toEpochMilli()
                + (extensao.startsWith(".") ? extensao : "." + extensao);
        String uploadUrl = minioService.getUploadPresignedUrl(filename, 15); // Expira em 15 min

        return new com.bruno.artistalbum.dto.PresignedUploadDTO(uploadUrl, filename);
    }

    @Transactional
    public AlbumDTO confirmarImagem(UUID id, String filename) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Album não encontrado"));

        if (album.getUrlsImagens() == null) {
            album.setUrlsImagens(new java.util.ArrayList<>());
        }
        album.getUrlsImagens().add(filename);
        album = albumRepository.save(album);

        return new AlbumDTO(album, minioService);
    }
}
