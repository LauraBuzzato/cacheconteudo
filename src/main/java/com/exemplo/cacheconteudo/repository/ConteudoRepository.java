package com.exemplo.cacheconteudo.repository;

import com.exemplo.cacheconteudo.model.Conteudo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConteudoRepository
        extends JpaRepository<Conteudo, Long> {
}