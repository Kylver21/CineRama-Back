package com.utp.cinerama.cinerama.model;

public class Sala {
    private Long id;
    private String nombre;              // Nombre de la sala 
    private String descripcion;         // Descripción de la sala
    private Integer capacidadTotal;     // Cantidad total de asientos
    private TipoSala tipo;             // Tipo de sala 
    private Boolean estado;            // Activa/Inactiva

    public enum TipoSala {
        NORMAL("Normal"),
        SALA_2D("2D");

        private final String descripcion;

        TipoSala(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    // Constructor vacío
    public Sala() {
    }

    // Constructor completo
    public Sala(Long id, String nombre, String descripcion, Integer capacidadTotal, 
               TipoSala tipo, Boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.capacidadTotal = capacidadTotal;
        this.tipo = tipo;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Integer getCapacidadTotal() { return capacidadTotal; }
    public void setCapacidadTotal(Integer capacidadTotal) { this.capacidadTotal = capacidadTotal; }
    
    public TipoSala getTipo() { return tipo; }
    public void setTipo(TipoSala tipo) { this.tipo = tipo; }
    
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    // Métodos de negocio
    public boolean estaActiva() {
        return estado != null && estado;
    }

    public boolean es2D() {
        return tipo == TipoSala.SALA_2D;
    }

    public String getCapacidadFormateada() {
        return capacidadTotal + " asientos";
    }

    @Override
    public String toString() {
        return "Sala{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", tipo=" + tipo +
                ", capacidadTotal=" + capacidadTotal +
                ", estado=" + estado +
                '}';
    }
}
