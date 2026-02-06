package com.bruno.artistalbum.controller;

import com.bruno.artistalbum.dto.AlbumDTO;
import com.bruno.artistalbum.service.AlbumService;
import com.bruno.artistalbum.service.TokenService;
import com.bruno.artistalbum.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AlbumController.class)
@AutoConfigureMockMvc(addFilters = false)
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlbumService albumService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve buscar album por ID - GET /api/albuns/{id}")
    void deveBuscarAlbumPorId() throws Exception {
        UUID id = UUID.randomUUID();
        AlbumDTO dto = new AlbumDTO();
        dto.setId(id);
        dto.setTitulo("Album Teste");
        dto.setAnoLancamento(2000);

        when(albumService.buscarPorId(id)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/albuns/" + id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Album Teste"));
    }

    @Test
    @DisplayName("Deve criar album - POST /api/v1/albuns")
    void deveCriarAlbum() throws Exception {
        AlbumDTO input = new AlbumDTO();
        input.setTitulo("Novo Album");
        input.setAnoLancamento(2024);

        AlbumDTO output = new AlbumDTO();
        output.setId(UUID.randomUUID());
        output.setTitulo("Novo Album");
        output.setAnoLancamento(2024);

        when(albumService.salvar(any(AlbumDTO.class))).thenReturn(output);

        mockMvc.perform(post("/api/v1/albuns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").value("Novo Album"));
    }
}
