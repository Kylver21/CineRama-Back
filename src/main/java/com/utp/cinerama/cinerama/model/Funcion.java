package com.utp.cinerama.cinerama.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Funcion {
    private Long id;
    private Pelicula pelicula;
    private Sala sala;                  // Cambio: ahora es una relación con la entidad Sala
    private LocalDate fecha;
    private LocalTime hora;
    private Integer asientosDisponibles;
    private Integer asientosTotales;

    public Funcion() {
    }

    public Funcion(Long id, Pelicula pelicula, Sala sala, LocalDate fecha, 
                  LocalTime hora, Integer asientosDisponibles, Integer asientosTotales) {
        this.id = id;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fecha = fecha;
        this.hora = hora;
        this.asientosDisponibles = asientosDisponibles;
        this.asientosTotales = asientosTotales;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Pelicula getPelicula() { return pelicula; }
    public void setPelicula(Pelicula pelicula) { this.pelicula = pelicula; }
    
    public Sala getSala() { return sala; }
    public void setSala(Sala sala) { this.sala = sala; }
    
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    
    public Integer getAsientosDisponibles() { return asientosDisponibles; }
    public void setAsientosDisponibles(Integer asientosDisponibles) { this.asientosDisponibles = asientosDisponibles; }
    
    public Integer getAsientosTotales() { return asientosTotales; }
    public void setAsientosTotales(Integer asientosTotales) { this.asientosTotales = asientosTotales; }

    // Métodos de negocio
    public boolean hayAsientosDisponibles() {
        return asientosDisponibles != null && asientosDisponibles > 0;
    }

    public boolean reservarAsiento() {
        if (hayAsientosDisponibles()) {
            asientosDisponibles--;
            return true;
        }
        return false;
    }

    public void liberarAsiento() {
        if (asientosDisponibles < asientosTotales) {
            asientosDisponibles++;
        }
    }

    public double getPorcentajeOcupacion() {
        if (asientosTotales == null || asientosTotales == 0) return 0.0;
        int ocupados = asientosTotales - (asientosDisponibles != null ? asientosDisponibles : 0);
        return (ocupados * 100.0) / asientosTotales;
    }

    @Override
    public String toString() {
        return "Funcion{" +
                "id=" + id +
                ", pelicula=" + (pelicula != null ? pelicula.getTitulo() : "null") +
                ", sala=" + (sala != null ? sala.getNombre() : "null") +
                ", fecha=" + fecha +
                ", hora=" + hora +
                ", asientosDisponibles=" + asientosDisponibles +
                ", asientosTotales=" + asientosTotales +
                '}';
    }
}
