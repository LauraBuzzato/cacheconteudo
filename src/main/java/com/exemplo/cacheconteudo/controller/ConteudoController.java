package com.exemplo.cacheconteudo.controller;

import com.exemplo.cacheconteudo.dto.ConteudoRequest;
import com.exemplo.cacheconteudo.dto.ConteudoResponse;
import com.exemplo.cacheconteudo.mapper.ConteudoMapper;
import com.exemplo.cacheconteudo.model.Conteudo;
import com.exemplo.cacheconteudo.service.ConteudoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/conteudos")
public class ConteudoController {

    private final ConteudoService service;

    public ConteudoController(ConteudoService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConteudoResponse> buscar(
            @PathVariable Long id
    ) {
        AtomicReference<String> resultado =
                new AtomicReference<>();

        Conteudo conteudo = service.buscar(id, resultado::set);

        return ResponseEntity.ok()
                .header("X-Cache", resultado.get())
                .body(ConteudoMapper.toDto(conteudo));
    }

    @PostMapping
    public ResponseEntity<ConteudoResponse> cadastrar(
            @RequestBody ConteudoRequest dto
    ) {
        Conteudo entidade = ConteudoMapper.toEntity(dto);
        Conteudo salvo = service.cadastrar(entidade);

        return ResponseEntity
                .created(URI.create("/conteudos/" + salvo.id))
                .body(ConteudoMapper.toDto(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConteudoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody ConteudoRequest dto
    ) {
        Conteudo entidade = ConteudoMapper.toEntity(dto);
        Conteudo atualizado = service.atualizar(id, entidade);

        return ResponseEntity.ok(
                ConteudoMapper.toDto(atualizado)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}