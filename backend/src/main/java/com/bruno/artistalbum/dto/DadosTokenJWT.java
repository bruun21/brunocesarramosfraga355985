package com.bruno.artistalbum.dto;

public record DadosTokenJWT(String token, String refreshToken) {
    public DadosTokenJWT(String token) {
        this(token, null);
    }
}
