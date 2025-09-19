package com.utp.cinerama.cinerama.model;

import java.math.BigDecimal;

public class Pelicula {
    private Long id;
    private String titulo;
    private String genero;
    private Integer duracion; 
    private String clasificacion;
    private String sinopsis;
    private BigDecimal precio;

    
    public Pelicula() {
    }

   
    public Pelicula(Long id, String titulo, String genero, Integer duracion, 
                   String clasificacion, String sinopsis, BigDecimal precio) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.duracion = duracion;
        this.clasificacion = clasificacion;
        this.sinopsis = sinopsis;
        this.precio = precio;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    
    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }
    
    public String getClasificacion() { return clasificacion; }
    public void setClasificacion(String clasificacion) { this.clasificacion = clasificacion; }
    
    public String getSinopsis() { return sinopsis; }
    public void setSinopsis(String sinopsis) { this.sinopsis = sinopsis; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    // Método útil para mostrar duración formateada
    public String getDuracionFormateada() {
        if (duracion == null) return "0h 0m";
        int horas = duracion / 60;
        int minutos = duracion % 60;
        return horas + "h " + minutos + "m";
    }

    @Override
    public String toString() {
        return "Pelicula{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", genero='" + genero + '\'' +
                ", duracion=" + duracion +
                ", clasificacion='" + clasificacion + '\'' +
                ", precio=" + precio +
                '}';
    }
}