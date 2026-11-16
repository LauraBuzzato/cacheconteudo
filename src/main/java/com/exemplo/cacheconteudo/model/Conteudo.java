package com.exemplo.cacheconteudo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Conteudo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String titulo;

    @Column(length = 10000)
    public String corpo;

    public String autor;

    public LocalDateTime atualizadoEm;
}