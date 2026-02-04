package com.bruno.artistalbum.service;

import com.bruno.artistalbum.dto.ArtistaDTO;
import com.bruno.artistalbum.model.Artista;
import com.bruno.artistalbum.repository.ArtistaRepository;
import com.bruno.artistalbum.exception.RegraDeNegocioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ArtistaService {

    @Autowired
    private ArtistaRepository artistaRepository;

    @Transactional(readOnly = true)
    public Page<ArtistaDTO> listarTodos(Pageable paginacao) {
        return artistaRepository.findAll(paginacao).map(ArtistaDTO::new);
    }

    @Transactional(readOnly = true)
    public ArtistaDTO buscarPorId(UUID id) {
        Artista artista = artistaRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Artista não encontrado"));
        return new ArtistaDTO(artista);
    }

    @Transactional
    public ArtistaDTO salvar(ArtistaDTO dadosArtista) {
        Artista artista = new Artista();
        artista.setNome(dadosArtista.getNome());
        artista = artistaRepository.save(artista);
        return new ArtistaDTO(artista);
    }

    @Transactional
    public ArtistaDTO atualizar(UUID id, ArtistaDTO dadosArtista) {
        Artista artista = artistaRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Artista não encontrado"));
        artista.setNome(dadosArtista.getNome());
        artista = artistaRepository.save(artista);
        return new ArtistaDTO(artista);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!artistaRepository.existsById(id)) {
            throw new RegraDeNegocioException("Artista não encontrado");
        }
        artistaRepository.deleteById(id);
    }
}
