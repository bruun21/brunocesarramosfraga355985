package com.bruno.artistalbum.controller;

import com.bruno.artistalbum.dto.ArtistaDTO;
import com.bruno.artistalbum.service.ArtistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/artistas")
public class ArtistaController {

    @Autowired
    private ArtistaService artistaService;

    @GetMapping
    public ResponseEntity<Page<ArtistaDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable paginacao) {
        return ResponseEntity.ok(artistaService.listarTodos(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistaDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(artistaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ArtistaDTO> salvar(@RequestBody ArtistaDTO dadosArtista) {
        return ResponseEntity.status(HttpStatus.CREATED).body(artistaService.salvar(dadosArtista));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistaDTO> atualizar(@PathVariable UUID id, @RequestBody ArtistaDTO dadosArtista) {
        return ResponseEntity.ok(artistaService.atualizar(id, dadosArtista));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        artistaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
