package com.exemplo.cacheconteudo.cache;

import com.exemplo.cacheconteudo.model.Conteudo;

public class EntradaCache {

    private final Conteudo conteudo;
    private final long armazenadoEm;
    private long ultimoAcesso;

    public EntradaCache(Conteudo conteudo, long agora) {
        this.conteudo = conteudo;
        this.armazenadoEm = agora;
        this.ultimoAcesso = agora;
    }

    public Conteudo getConteudo() {
        return conteudo;
    }

    public long getArmazenadoEm() {
        return armazenadoEm;
    }

    public long getUltimoAcesso() {
        return ultimoAcesso;
    }

    public void registrarAcesso(long agora) {
        this.ultimoAcesso = agora;
    }
}