package com.exemplo.cacheconteudo.dto;

public record ConteudoRequest(
        String titulo,
        String corpo,
        String autor
) {
}