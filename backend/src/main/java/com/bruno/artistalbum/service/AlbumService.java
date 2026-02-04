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
import java.io.IOException;
import java.time.Instant;

import java.util.UUID;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private MinioService minioService;

    @Transactional(readOnly = true)
    public Page<AlbumDTO> listarTodos(Pageable paginacao) {
        return albumRepository.findAll(paginacao).map(AlbumDTO::new);
    }

    @Transactional(readOnly = true)
    public AlbumDTO buscarPorId(UUID id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Album não encontrado"));
        return new AlbumDTO(album);
    }

    @Transactional
    public AlbumDTO salvar(AlbumDTO dadosAlbum) {
        Album album = new Album();
        album.setTitulo(dadosAlbum.getTitulo());
        album.setAnoLancamento(dadosAlbum.getAnoLancamento());
        album.setUrlsImagens(dadosAlbum.getUrlsImagens());
        album = albumRepository.save(album);
        return new AlbumDTO(album);
    }

    @Transactional
    public AlbumDTO atualizar(UUID id, AlbumDTO dadosAlbum) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Album não encontrado"));
        album.setTitulo(dadosAlbum.getTitulo());
        album.setAnoLancamento(dadosAlbum.getAnoLancamento());
        album.setUrlsImagens(dadosAlbum.getUrlsImagens());
        album = albumRepository.save(album);
        return new AlbumDTO(album);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!albumRepository.existsById(id)) {
            throw new RegraDeNegocioException("Album não encontrado");
        }
        albumRepository.deleteById(id);
    }

    @Transactional
    public AlbumDTO adicionarImagem(UUID id, MultipartFile imagem) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Album não encontrado"));

        String filename = "album-" + id + "-" + Instant.now().toEpochMilli() + "-" + imagem.getOriginalFilename();

        try {
            String url = minioService.uploadFile(filename, imagem.getInputStream(), imagem.getContentType());

            if (album.getUrlsImagens() == null) {
                album.setUrlsImagens(new java.util.ArrayList<>());
            }
            album.getUrlsImagens().add(url);

            album = albumRepository.save(album);
            return new AlbumDTO(album);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar imagem", e);
        }
    }
}
