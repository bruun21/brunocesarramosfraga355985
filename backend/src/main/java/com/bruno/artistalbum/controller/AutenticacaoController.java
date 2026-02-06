package com.bruno.artistalbum.controller;

import com.bruno.artistalbum.dto.DadosAutenticacao;
import com.bruno.artistalbum.dto.DadosTokenJWT;
import com.bruno.artistalbum.model.Usuario;
import com.bruno.artistalbum.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private com.bruno.artistalbum.repository.UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = manager.authenticate(authenticationToken);

        var usuario = (Usuario) authentication.getPrincipal();
        var tokenJWT = tokenService.gerarToken(usuario);
        var refreshToken = tokenService.gerarRefreshToken(usuario);

        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<DadosTokenJWT> renovarToken(@RequestBody DadosTokenJWT dados) {
        String email = tokenService.getSubject(dados.refreshToken());
        Usuario usuario = (Usuario) usuarioRepository.findByEmail(email);
        String novoToken = tokenService.gerarToken(usuario);
        String novoRefreshToken = tokenService.gerarRefreshToken(usuario);
        return ResponseEntity.ok(new DadosTokenJWT(novoToken, novoRefreshToken));
    }

}
