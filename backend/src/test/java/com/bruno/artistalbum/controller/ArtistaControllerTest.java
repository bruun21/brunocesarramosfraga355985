package com.bruno.artistalbum.controller;

import com.bruno.artistalbum.dto.ArtistaDTO;
import com.bruno.artistalbum.service.ArtistaService;
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

@WebMvcTest(controllers = ArtistaController.class)
@AutoConfigureMockMvc(addFilters = false)
class ArtistaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArtistaService artistaService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve buscar artista por ID - GET /api/artistas/{id}")
    void deveBuscarArtistaPorId() throws Exception {
        UUID id = UUID.randomUUID();
        ArtistaDTO dto = new ArtistaDTO();
        dto.setId(id);
        dto.setNome("Banda Teste");
        dto.setTipo(com.bruno.artistalbum.model.TipoArtista.BANDA);

        when(artistaService.buscarPorId(id)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/artistas/" + id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Banda Teste"));
    }

    @Test
    @DisplayName("Deve criar artista - POST /api/v1/artistas")
    void deveCriarArtista() throws Exception {
        ArtistaDTO input = new ArtistaDTO();
        input.setNome("Novo Artista");
        input.setTipo(com.bruno.artistalbum.model.TipoArtista.CANTOR);

        ArtistaDTO output = new ArtistaDTO();
        output.setId(UUID.randomUUID());
        output.setNome("Novo Artista");
        output.setTipo(com.bruno.artistalbum.model.TipoArtista.CANTOR);

        when(artistaService.salvar(any(ArtistaDTO.class))).thenReturn(output);

        mockMvc.perform(post("/api/v1/artistas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Novo Artista"));
    }
}
