package com.bruno.artistalbum.service;

import com.bruno.artistalbum.dto.ArtistaDTO;
import com.bruno.artistalbum.model.Artista;
import com.bruno.artistalbum.repository.ArtistaRepository;
import com.bruno.artistalbum.exception.RegraDeNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistaServiceTest {

    @Mock
    private ArtistaRepository artistaRepository;

    @InjectMocks
    private ArtistaService artistaService;

    @Test
    @DisplayName("Deve salvar artista com sucesso")
    void deveSalvarArtista() {
        ArtistaDTO dto = new ArtistaDTO(null, "Artista Teste", null);
        Artista artistaSalvo = new Artista();
        artistaSalvo.setId(UUID.randomUUID());
        artistaSalvo.setNome(dto.getNome());

        when(artistaRepository.save(any(Artista.class))).thenReturn(artistaSalvo);

        ArtistaDTO resultado = artistaService.salvar(dto);

        assertNotNull(resultado.getId());
        assertEquals(dto.getNome(), resultado.getNome());
        verify(artistaRepository).save(any(Artista.class));
    }

    @Test
    @DisplayName("Deve listar todos com paginação")
    void deveListarTodos() {
        Pageable pageable = PageRequest.of(0, 10);
        Artista artista = new Artista();
        artista.setNome("Teste");
        Page<Artista> page = new PageImpl<>(List.of(artista));

        when(artistaRepository.findAll(pageable)).thenReturn(page);

        Page<ArtistaDTO> resultado = artistaService.listarTodos(pageable);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getContent().size());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar ID inexistente")
    void deveFalharAoDeletarInexistente() {
        UUID id = UUID.randomUUID();
        when(artistaRepository.existsById(id)).thenReturn(false);

        assertThrows(RegraDeNegocioException.class, () -> artistaService.deletar(id));
    }
}
