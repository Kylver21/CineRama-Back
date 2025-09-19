package com.utp.cinerama.cinerama.model;

import java.math.BigDecimal;

public class Producto {
    private Long id;
    private String nombre;
    private String descripcion;
    private CategoriaProducto categoria;
    private BigDecimal precio;
    private Integer stock;
    private Boolean activo;
    private String imagenUrl;  // URL de la imagen del producto

    public enum CategoriaProducto {
        PALOMITAS("Palomitas"),
        BEBIDAS("Bebidas"),
        CHOCOLATE("Chocolate"),
        GOLOSINAS("Golosinas"),
        COMBOS("Combos");

        private final String descripcion;

        CategoriaProducto(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    public Producto() {
    }

    public Producto(Long id, String nombre, String descripcion, CategoriaProducto categoria,
                   BigDecimal precio, Integer stock, Boolean activo, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.activo = activo;
        this.imagenUrl = imagenUrl;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public CategoriaProducto getCategoria() { return categoria; }
    public void setCategoria(CategoriaProducto categoria) { this.categoria = categoria; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    // Métodos de negocio
    public boolean tieneStock() {
        return stock != null && stock > 0;
    }
    
    public boolean esCombo() {
        return categoria == CategoriaProducto.COMBOS;
    }
    
    public boolean reducirStock(int cantidad) {
        if (stock >= cantidad) {
            stock -= cantidad;
            return true;
        }
        return false;
    }
    
    public void aumentarStock(int cantidad) {
        if (stock == null) {
            stock = 0;
        }
        stock += cantidad;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", categoria=" + categoria +
                ", precio=" + precio +
                ", stock=" + stock +
                ", activo=" + activo +
                '}';
    }
}