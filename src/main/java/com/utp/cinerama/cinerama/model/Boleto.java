package com.utp.cinerama.cinerama.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Boleto {
    private Long id;
    private Funcion funcion;
    private String asiento; 
    private BigDecimal precio;
    private EstadoBoleto estado;
    private LocalDateTime fechaCompra; 
    private Cliente cliente;

    public enum EstadoBoleto {
        RESERVADO("Reservado"),
        PAGADO("Pagado"),
        CANCELADO("Cancelado"),
        USADO("Usado");

        private final String descripcion;

        EstadoBoleto(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    public Boleto() {
    }

    public Boleto(Long id, Funcion funcion, String asiento, BigDecimal precio, 
                  EstadoBoleto estado, LocalDateTime fechaCompra, Cliente cliente) {
        this.id = id;
        this.funcion = funcion;
        this.asiento = asiento;
        this.precio = precio;
        this.estado = estado;
        this.fechaCompra = fechaCompra;
        this.cliente = cliente;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Funcion getFuncion() { return funcion; }
    public void setFuncion(Funcion funcion) { this.funcion = funcion; }
    
    public String getAsiento() { return asiento; }
    public void setAsiento(String asiento) { this.asiento = asiento; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public EstadoBoleto getEstado() { return estado; }
    public void setEstado(EstadoBoleto estado) { this.estado = estado; }
    
    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDateTime fechaCompra) { this.fechaCompra = fechaCompra; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    // Métodos de negocio
    public boolean esValido() {
        return estado == EstadoBoleto.PAGADO || estado == EstadoBoleto.RESERVADO;
    }

    public void marcarComoUsado() {
        if (estado == EstadoBoleto.PAGADO) {
            this.estado = EstadoBoleto.USADO;
        }
    }

    public void cancelar() {
        if (estado == EstadoBoleto.RESERVADO || estado == EstadoBoleto.PAGADO) {
            this.estado = EstadoBoleto.CANCELADO;
        }
    }

    public boolean puedeSerCancelado() {
        return estado == EstadoBoleto.RESERVADO || estado == EstadoBoleto.PAGADO;
    }

    @Override
    public String toString() {
        return "Boleto{" +
                "id=" + id +
                ", funcion=" + (funcion != null ? funcion.getId() : "null") +
                ", asiento='" + asiento + '\'' +
                ", precio=" + precio +
                ", estado=" + estado +
                ", fechaCompra=" + fechaCompra +
                ", cliente=" + (cliente != null ? cliente.getNombre() : "null") +
                '}';
    }
}
