package com.bruno.artistalbum.repository;

import com.bruno.artistalbum.model.Artista;
import com.bruno.artistalbum.model.TipoArtista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtistaRepository extends JpaRepository<Artista, UUID> {
    Page<Artista> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<Artista> findByTipo(TipoArtista tipo, Pageable pageable);

    java.util.List<Artista> findAllById(Iterable<UUID> ids);
}
