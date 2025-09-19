package com.utp.cinerama.cinerama.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VentaProducto {
    private Long id;
    private List<DetalleVentaProducto> detalles = new ArrayList<>();
    private BigDecimal total;
    private LocalDateTime fechaVenta;
    private Cliente cliente; // Opcional, puede ser null para ventas sin registro
    private String metodoPago; // "EFECTIVO", "TARJETA", etc.
    private Boolean completada;

    public static class DetalleVentaProducto {
        private Producto producto;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;

        public DetalleVentaProducto() {
        }

        public DetalleVentaProducto(Producto producto, Integer cantidad, BigDecimal precioUnitario) {
            this.producto = producto;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            calcularSubtotal();
        }

        private void calcularSubtotal() {
            this.subtotal = this.precioUnitario.multiply(new BigDecimal(this.cantidad));
        }

        // Getters y Setters
        public Producto getProducto() { return producto; }
        public void setProducto(Producto producto) { this.producto = producto; }
        
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { 
            this.cantidad = cantidad;
            calcularSubtotal();
        }
        
        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { 
            this.precioUnitario = precioUnitario;
            calcularSubtotal();
        }
        
        public BigDecimal getSubtotal() { return subtotal; }
    }

    public VentaProducto() {
        this.fechaVenta = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
        this.completada = false;
    }

    public VentaProducto(Long id, Cliente cliente, String metodoPago) {
        this();
        this.id = id;
        this.cliente = cliente;
        this.metodoPago = metodoPago;
    }

    // Métodos de negocio
    public void agregarProducto(Producto producto, Integer cantidad) {
        if (producto == null || cantidad <= 0) {
            throw new IllegalArgumentException("Producto inválido o cantidad debe ser mayor a 0");
        }
        
        // Verificar si el producto ya existe en la venta
        for (DetalleVentaProducto detalle : detalles) {
            if (detalle.getProducto().getId().equals(producto.getId())) {
                // Sumar la cantidad
                detalle.setCantidad(detalle.getCantidad() + cantidad);
                calcularTotal();
                return;
            }
        }
        
        // Si no existe, agregar nuevo detalle
        DetalleVentaProducto nuevoDetalle = new DetalleVentaProducto(
            producto, cantidad, producto.getPrecio()
        );
        detalles.add(nuevoDetalle);
        calcularTotal();
    }

    public void eliminarProducto(Long productoId) {
        detalles.removeIf(detalle -> detalle.getProducto().getId().equals(productoId));
        calcularTotal();
    }

    public void actualizarCantidadProducto(Long productoId, Integer nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            eliminarProducto(productoId);
            return;
        }
        
        for (DetalleVentaProducto detalle : detalles) {
            if (detalle.getProducto().getId().equals(productoId)) {
                detalle.setCantidad(nuevaCantidad);
                calcularTotal();
                return;
            }
        }
    }

    public void calcularTotal() {
        this.total = BigDecimal.ZERO;
        for (DetalleVentaProducto detalle : detalles) {
            this.total = this.total.add(detalle.getSubtotal());
        }
    }

    public void completarVenta() {
        this.completada = true;
        this.fechaVenta = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public List<DetalleVentaProducto> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVentaProducto> detalles) { 
        this.detalles = detalles;
        calcularTotal();
    }
    
    public BigDecimal getTotal() { return total; }
    
    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    
    public Boolean getCompletada() { return completada; }
    public void setCompletada(Boolean completada) { this.completada = completada; }
    
    public int getCantidadTotal() {
        return detalles.stream().mapToInt(DetalleVentaProducto::getCantidad).sum();
    }
}