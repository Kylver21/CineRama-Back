package com.utp.cinerama.cinerama.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pago {
    private Long id;
    private String referencia;        
    private BigDecimal monto;         
    private Cliente cliente;          
    private MetodoPago metodo;        
    private String numeroTarjeta;     
    private EstadoPago estado;        
    private LocalDateTime fechaPago;  
    private TipoComprobante tipoComprobante;  
    
    // Tipos de métodos de pago disponibles
    public enum MetodoPago {
        TARJETA_CREDITO("Tarjeta de Crédito"),
        TARJETA_DEBITO("Tarjeta de Débito"),
        APP_YAPE("Yape"),
        APP_PLIN("Plin"),  
        EFECTIVO("Efectivo");

        private final String descripcion;

        MetodoPago(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }
    
    // Estados posibles del pago
    public enum EstadoPago {
        PENDIENTE("Pendiente"),
        COMPLETADO("Completado"),
        RECHAZADO("Rechazado");

        private final String descripcion;

        EstadoPago(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }
    
    // Tipos de comprobante
    public enum TipoComprobante {
        BOLETA("Boleta simple"),
        FACTURA("Factura");

        private final String descripcion;

        TipoComprobante(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    // Constructor vacío
    public Pago() {
        this.fechaPago = LocalDateTime.now();
        this.estado = EstadoPago.PENDIENTE;
        this.referencia = "TRX" + System.currentTimeMillis() % 10000;
    }

    // Constructor completo
    public Pago(Long id, BigDecimal monto, Cliente cliente, MetodoPago metodo, 
               String numeroTarjeta, TipoComprobante tipoComprobante) {
        this();
        this.id = id;
        this.monto = monto;
        this.cliente = cliente;
        this.metodo = metodo;
        setNumeroTarjeta(numeroTarjeta);  // Usamos el setter para enmascaramiento
        this.tipoComprobante = tipoComprobante;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public MetodoPago getMetodo() { return metodo; }
    public void setMetodo(MetodoPago metodo) { this.metodo = metodo; }
    
    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) {
        // Almacenar solo los últimos 4 dígitos por seguridad
        if (numeroTarjeta != null && numeroTarjeta.length() >= 4) {
            this.numeroTarjeta = "****" + numeroTarjeta.substring(numeroTarjeta.length() - 4);
        } else {
            this.numeroTarjeta = numeroTarjeta;
        }
    }
    
    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }
    
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
    
    public TipoComprobante getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(TipoComprobante tipoComprobante) { this.tipoComprobante = tipoComprobante; }
    
    // Métodos básicos de negocio
    public void completarPago() {
        this.estado = EstadoPago.COMPLETADO;
        this.fechaPago = LocalDateTime.now();
    }
    
    public void rechazarPago() {
        this.estado = EstadoPago.RECHAZADO;
    }
    
    public boolean estaPagado() {
        return estado == EstadoPago.COMPLETADO;
    }
    
    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", referencia='" + referencia + '\'' +
                ", monto=" + monto +
                ", cliente=" + (cliente != null ? cliente.getNombre() : "null") +
                ", metodo=" + metodo +
                ", numeroTarjeta='" + numeroTarjeta + '\'' +
                ", estado=" + estado +
                ", fechaPago=" + fechaPago +
                ", tipoComprobante=" + tipoComprobante +
                '}';
    }
}