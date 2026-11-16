package com.exemplo.cacheconteudo.service;

import com.exemplo.cacheconteudo.cache.ConteudoCache;
import com.exemplo.cacheconteudo.model.Conteudo;
import com.exemplo.cacheconteudo.repository.ConteudoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Service
public class ConteudoService {

    private final ConteudoRepository repository;
    private final ConteudoCache cache;

    public ConteudoService(
            ConteudoRepository repository,
            ConteudoCache cache
    ) {
        this.repository = repository;
        this.cache = cache;
    }

    public synchronized Conteudo buscar(
            Long id,
            Consumer<String> informarResultado
    ) {
        return cache.buscar(
                id,
                () -> buscarNaFonte(id),
                informarResultado
        );
    }

    private Conteudo buscarNaFonte(Long id) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Busca interrompida"
            );
        }

        return encontrar(id);
    }

    private Conteudo encontrar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Conteúdo não encontrado"
                ));
    }

    public synchronized Conteudo cadastrar(Conteudo conteudo) {
        conteudo.atualizadoEm = LocalDateTime.now();
        return repository.save(conteudo);
    }

    public synchronized Conteudo atualizar(
            Long id,
            Conteudo novosDados
    ) {
        Conteudo existente = encontrar(id);

        existente.titulo = novosDados.titulo;
        existente.corpo = novosDados.corpo;
        existente.autor = novosDados.autor;
        existente.atualizadoEm = LocalDateTime.now();

        Conteudo atualizado = repository.save(existente);
        cache.invalidar(id);

        return atualizado;
    }

    public synchronized void excluir(Long id) {
        Conteudo existente = encontrar(id);
        repository.delete(existente);
        cache.invalidar(id);
    }
}