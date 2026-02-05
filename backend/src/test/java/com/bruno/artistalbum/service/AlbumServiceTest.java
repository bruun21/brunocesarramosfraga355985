package com.bruno.artistalbum.service;

import com.bruno.artistalbum.dto.AlbumDTO;
import com.bruno.artistalbum.model.Album;
import com.bruno.artistalbum.repository.AlbumRepository;
import com.bruno.artistalbum.exception.RegraDeNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private MinioService minioService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private AlbumService albumService;

    @Test
    @DisplayName("Deve buscar album por ID")
    void deveBuscarPorId() {
        UUID id = UUID.randomUUID();
        Album album = new Album();
        album.setId(id);

        when(albumRepository.findById(id)).thenReturn(Optional.of(album));

        AlbumDTO result = albumService.buscarPorId(id);

        assertEquals(id, result.getId());
    }

    @Test
    @DisplayName("Deve adicionar imagem e notificar WebSocket")
    void deveAdicionarImagem() throws IOException {
        UUID id = UUID.randomUUID();
        Album album = new Album();
        album.setId(id);
        album.setUrlsImagens(new ArrayList<>());

        MultipartFile imagem = mock(MultipartFile.class);
        when(imagem.getOriginalFilename()).thenReturn("test.jpg");
        when(imagem.getContentType()).thenReturn("image/jpeg");
        when(imagem.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        when(albumRepository.findById(id)).thenReturn(Optional.of(album));
        when(minioService.uploadFile(anyString(), any(InputStream.class), anyString())).thenReturn("http://minio/url");
        when(albumRepository.save(any(Album.class))).thenReturn(album);

        albumService.adicionarImagem(id, imagem);

        verify(minioService).uploadFile(anyString(), any(), anyString());
        verify(messagingTemplate).convertAndSend(eq("/topic/albuns/" + id), any(AlbumDTO.class));
        verify(albumRepository).save(album);
    }
}
