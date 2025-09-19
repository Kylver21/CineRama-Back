package com.utp.cinerama.cinerama.model;

public class Cliente {
    private Long id;
    private String nombre;
    private String apellido;        
    private String email;
    private String telefono;
    private String numeroDocumento;  
    private TipoDocumento tipoDocumento; 

    // Enum para tipo de documento
    public enum TipoDocumento {
        DNI("DNI"),
        PASAPORTE("Pasaporte"),
        CARNET_EXTRANJERIA("Carnet de Extranjería");

        private final String descripcion;

        TipoDocumento(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    public Cliente() {
    }

    public Cliente(Long id, String nombre, String apellido, String email, String telefono, 
                  String numeroDocumento, TipoDocumento tipoDocumento) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.numeroDocumento = numeroDocumento;
        this.tipoDocumento = tipoDocumento;
    }

    // Getters y Setters originales
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    // Nuevos getters y setters
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    
    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    
    
    public boolean emailValido() {
        return email != null && email.contains("@") && email.contains(".");
    }

    public boolean telefonoValido() {
        return telefono != null && telefono.matches("\\d{9}");
    }
    
    public boolean documentoValido() {
        if (numeroDocumento == null) return false;
        
        switch (tipoDocumento) {
            case DNI:
                return numeroDocumento.matches("\\d{8}");
            case PASAPORTE:
                return numeroDocumento.length() >= 6 && numeroDocumento.length() <= 12;
            case CARNET_EXTRANJERIA:
                return numeroDocumento.matches("\\d{9}");
            default:
                return false;
        }
    }
    
    
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", documento='" + tipoDocumento + ":" + numeroDocumento + '\'' +
                '}';
    }
}