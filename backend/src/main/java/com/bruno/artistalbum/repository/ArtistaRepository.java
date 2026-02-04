package com.bruno.artistalbum.repository;

import com.bruno.artistalbum.model.Artista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtistaRepository extends JpaRepository<Artista, UUID> {
}
