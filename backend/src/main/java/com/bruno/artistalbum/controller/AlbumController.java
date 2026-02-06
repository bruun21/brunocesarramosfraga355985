package com.bruno.artistalbum.controller;

import com.bruno.artistalbum.dto.AlbumDTO;
import com.bruno.artistalbum.dto.PresignedUploadDTO;
import com.bruno.artistalbum.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/albuns")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @GetMapping
    public ResponseEntity<Page<AlbumDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "anoLancamento", direction = Sort.Direction.DESC) Pageable paginacao) {
        return ResponseEntity.ok(albumService.listarTodos(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(albumService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AlbumDTO> salvar(@RequestBody AlbumDTO dadosAlbum) {
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.salvar(dadosAlbum));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumDTO> atualizar(@PathVariable UUID id, @RequestBody AlbumDTO dadosAlbum) {
        return ResponseEntity.ok(albumService.atualizar(id, dadosAlbum));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        albumService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/imagem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlbumDTO> adicionarImagem(@PathVariable UUID id,
            @RequestParam("imagem") MultipartFile imagem) {
        return ResponseEntity.ok(albumService.adicionarImagem(id, imagem));
    }

    @PostMapping("/{id}/presigned-upload")
    public ResponseEntity<PresignedUploadDTO> obterUrlUpload(@PathVariable UUID id,
            @RequestParam("extensao") String extensao) {
        return ResponseEntity.ok(albumService.obterUrlUpload(id, extensao));
    }

    @PostMapping("/{id}/confirmar-imagem")
    public ResponseEntity<AlbumDTO> confirmarImagem(@PathVariable UUID id, @RequestParam("filename") String filename) {
        return ResponseEntity.ok(albumService.confirmarImagem(id, filename));
    }
}
