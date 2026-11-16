package com.exemplo.cacheconteudo.mapper;

import com.exemplo.cacheconteudo.dto.ConteudoRequest;
import com.exemplo.cacheconteudo.dto.ConteudoResponse;
import com.exemplo.cacheconteudo.model.Conteudo;

public class ConteudoMapper {

    public static Conteudo toEntity(ConteudoRequest dto) {
        Conteudo conteudo = new Conteudo();
        conteudo.titulo = dto.titulo();
        conteudo.corpo = dto.corpo();
        conteudo.autor = dto.autor();
        return conteudo;
    }

    public static ConteudoResponse toDto(Conteudo conteudo) {
        return new ConteudoResponse(
                conteudo.id,
                conteudo.titulo,
                conteudo.corpo,
                conteudo.autor,
                conteudo.atualizadoEm
        );
    }
}