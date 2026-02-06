package com.bruno.artistalbum.repository;

import com.bruno.artistalbum.model.Album;
import com.bruno.artistalbum.model.TipoArtista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {
    Page<Album> findByArtistasTipo(TipoArtista tipo, Pageable pageable);
}
