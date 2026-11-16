package com.exemplo.cacheconteudo.cache;

import com.exemplo.cacheconteudo.model.Conteudo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class ConteudoCache {

    private static final Logger log =
            LoggerFactory.getLogger(ConteudoCache.class);

    private final Map<Long, EntradaCache> entradas =
            new ConcurrentHashMap<>();

    private final int maxItens;
    private final long ttlNanos;

    private long hits;
    private long misses;
    private long evictions;

    public ConteudoCache(
            @Value("${app.cache.max-itens}") int maxItens,
            @Value("${app.cache.ttl-segundos}") long ttlSegundos
    ) {
        if (maxItens <= 0 || ttlSegundos <= 0) {
            throw new IllegalArgumentException(
                    "O limite e o TTL devem ser maiores que zero"
            );
        }

        this.maxItens = maxItens;
        this.ttlNanos = TimeUnit.SECONDS.toNanos(ttlSegundos);
    }

    public synchronized Conteudo buscar(
            Long id,
            Supplier<Conteudo> buscarNaFonte,
            Consumer<String> informarResultado
    ) {
        long inicio = System.nanoTime();
        String resultado = "MISS";

        try {
            EntradaCache entrada = entradas.get(id);
            long agora = System.nanoTime();

            if (entrada != null && !expirada(entrada, agora)) {
                hits++;
                resultado = "HIT";
                entrada.registrarAcesso(agora);
                informarResultado.accept(resultado);
                return entrada.getConteudo();
            }

            misses++;
            entradas.remove(id);
            informarResultado.accept(resultado);

            Conteudo conteudo = buscarNaFonte.get();

            removerExpirados();

            if (entradas.size() >= maxItens) {
                removerMenosRecente();
            }

            entradas.put(
                    id,
                    new EntradaCache(conteudo, System.nanoTime())
            );

            return conteudo;
        } finally {
            long tempoMs = TimeUnit.NANOSECONDS.toMillis(
                    System.nanoTime() - inicio
            );

            log.info(
                    "Cache {} - id={} - {} ms",
                    resultado,
                    id,
                    tempoMs
            );
        }
    }

    private boolean expirada(EntradaCache entrada, long agora) {
        return agora - entrada.getArmazenadoEm() >= ttlNanos;
    }

    private void removerExpirados() {
        long agora = System.nanoTime();

        entradas.entrySet().removeIf(
                item -> expirada(item.getValue(), agora)
        );
    }

    private void removerMenosRecente() {
        Long idRemovido = entradas.entrySet()
                .stream()
                .min(Comparator.comparingLong(
                        item -> item.getValue().getUltimoAcesso()
                ))
                .orElseThrow()
                .getKey();

        entradas.remove(idRemovido);
        evictions++;

        log.info("Cache EVICTION LRU - id={}", idRemovido);
    }

    public synchronized void invalidar(Long id) {
        entradas.remove(id);
        log.info("Cache INVALIDADO - id={}", id);
    }

    public synchronized void limpar() {
        entradas.clear();
        log.info("Cache LIMPO");
    }

    public synchronized Map<String, Object> estatisticas() {
        removerExpirados();

        long total = hits + misses;
        double hitRatio = total == 0
                ? 0.0
                : hits * 100.0 / total;

        Map<String, Object> dados = new LinkedHashMap<>();
        dados.put("tamanhoAtual", entradas.size());
        dados.put("limite", maxItens);
        dados.put("hits", hits);
        dados.put("misses", misses);
        dados.put("evictions", evictions);
        dados.put("hitRatio", hitRatio);

        return dados;
    }
}