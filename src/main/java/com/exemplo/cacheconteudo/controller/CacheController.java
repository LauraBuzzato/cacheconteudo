package com.exemplo.cacheconteudo.controller;

import com.exemplo.cacheconteudo.cache.ConteudoCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final ConteudoCache cache;

    public CacheController(ConteudoCache cache) {
        this.cache = cache;
    }

    @GetMapping("/estatisticas")
    public Map<String, Object> estatisticas() {
        return cache.estatisticas();
    }

    @DeleteMapping
    public ResponseEntity<Void> limpar() {
        cache.limpar();
        return ResponseEntity.noContent().build();
    }
}