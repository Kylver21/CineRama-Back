package com.utp.cinerama.cinerama.service;

import com.utp.cinerama.cinerama.model.Producto;
import com.utp.cinerama.cinerama.model.Producto.CategoriaProducto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private List<Producto> productos = new ArrayList<>();
    private AtomicLong contador = new AtomicLong(1);

    public ProductoService() {
        inicializarDatosPrueba();
    }

    // Métodos CRUD
    public List<Producto> obtenerTodosLosProductos() {
        return new ArrayList<>(productos);
    }

    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productos.stream()
                .filter(producto -> producto.getId().equals(id))
                .findFirst();
    }

    public Producto crearProducto(Producto producto) {
        // Verificar nombre único
        boolean existeNombre = productos.stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(producto.getNombre()));
        
        if (existeNombre) {
            throw new RuntimeException("Ya existe un producto con ese nombre: " + producto.getNombre());
        }

        producto.setId(contador.getAndIncrement());
        validarProducto(producto);
        productos.add(producto);
        return producto;
    }

    public Optional<Producto> actualizarProducto(Long id, Producto productoActualizado) {
        Optional<Producto> productoExistente = obtenerProductoPorId(id);
        
        if (productoExistente.isPresent()) {
            Producto producto = productoExistente.get();
            
            // Verificar nombre único (excluyendo el producto actual)
            boolean existeOtroNombre = productos.stream()
                    .anyMatch(p -> !p.getId().equals(id) && 
                             p.getNombre().equalsIgnoreCase(productoActualizado.getNombre()));
            
            if (existeOtroNombre) {
                throw new RuntimeException("Ya existe otro producto con ese nombre: " + productoActualizado.getNombre());
            }

            producto.setNombre(productoActualizado.getNombre());
            producto.setDescripcion(productoActualizado.getDescripcion());
            producto.setCategoria(productoActualizado.getCategoria());
            producto.setPrecio(productoActualizado.getPrecio());
            producto.setStock(productoActualizado.getStock());
            producto.setActivo(productoActualizado.getActivo());
            producto.setImagenUrl(productoActualizado.getImagenUrl());
            
            validarProducto(producto);
            return Optional.of(producto);
        }
        
        return Optional.empty();
    }

    public Optional<Producto> actualizarProductoParcial(Long id, Producto productoParcial) {
        Optional<Producto> productoExistente = obtenerProductoPorId(id);
        
        if (productoExistente.isPresent()) {
            Producto producto = productoExistente.get();
            
            if (productoParcial.getNombre() != null) {
                // Verificar nombre único
                boolean existeOtroNombre = productos.stream()
                        .anyMatch(p -> !p.getId().equals(id) && 
                                 p.getNombre().equalsIgnoreCase(productoParcial.getNombre()));
                
                if (existeOtroNombre) {
                    throw new RuntimeException("Ya existe otro producto con ese nombre: " + productoParcial.getNombre());
                }
                producto.setNombre(productoParcial.getNombre());
            }
            
            if (productoParcial.getDescripcion() != null) {
                producto.setDescripcion(productoParcial.getDescripcion());
            }
            
            if (productoParcial.getCategoria() != null) {
                producto.setCategoria(productoParcial.getCategoria());
            }
            
            if (productoParcial.getPrecio() != null) {
                producto.setPrecio(productoParcial.getPrecio());
            }
            
            if (productoParcial.getStock() != null) {
                producto.setStock(productoParcial.getStock());
            }
            
            if (productoParcial.getActivo() != null) {
                producto.setActivo(productoParcial.getActivo());
            }
            
            if (productoParcial.getImagenUrl() != null) {
                producto.setImagenUrl(productoParcial.getImagenUrl());
            }
            
            validarProducto(producto);
            return Optional.of(producto);
        }
        
        return Optional.empty();
    }

    public boolean eliminarProducto(Long id) {
        return productos.removeIf(producto -> producto.getId().equals(id));
    }

    // Métodos de búsqueda específicos
    public List<Producto> buscarPorCategoria(CategoriaProducto categoria) {
        return productos.stream()
                .filter(producto -> producto.getCategoria() == categoria)
                .collect(Collectors.toList());
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productos.stream()
                .filter(producto -> producto.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Producto> buscarProductosActivos() {
        return productos.stream()
                .filter(Producto::getActivo)
                .collect(Collectors.toList());
    }

    public List<Producto> buscarProductosConStock() {
        return productos.stream()
                .filter(Producto::tieneStock)
                .collect(Collectors.toList());
    }

    public List<Producto> buscarPorRangoPrecio(BigDecimal precioMin, BigDecimal precioMax) {
        return productos.stream()
                .filter(producto -> producto.getPrecio().compareTo(precioMin) >= 0 &&
                                  producto.getPrecio().compareTo(precioMax) <= 0)
                .collect(Collectors.toList());
    }

    // Métodos de inventario
    public boolean actualizarStock(Long id, int cantidad) {
        Optional<Producto> producto = obtenerProductoPorId(id);
        if (producto.isPresent()) {
            producto.get().setStock(cantidad);
            return true;
        }
        return false;
    }

    public boolean reducirStock(Long id, int cantidad) {
        Optional<Producto> producto = obtenerProductoPorId(id);
        if (producto.isPresent()) {
            return producto.get().reducirStock(cantidad);
        }
        return false;
    }

    public boolean aumentarStock(Long id, int cantidad) {
        Optional<Producto> producto = obtenerProductoPorId(id);
        if (producto.isPresent()) {
            producto.get().aumentarStock(cantidad);
            return true;
        }
        return false;
    }

    // Métodos auxiliares
    private void validarProducto(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio");
        }
        
        if (producto.getCategoria() == null) {
            throw new RuntimeException("La categoría es obligatoria");
        }
        
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El precio debe ser mayor a 0");
        }
        
        if (producto.getStock() == null) {
            producto.setStock(0);
        }
        
        if (producto.getActivo() == null) {
            producto.setActivo(true);
        }
    }

    private void inicializarDatosPrueba() {
        // Palomitas
        Producto producto1 = new Producto();
        producto1.setId(contador.getAndIncrement());
        producto1.setNombre("Palomitas Grandes");
        producto1.setDescripcion("Deliciosas palomitas de maíz con mantequilla");
        producto1.setCategoria(CategoriaProducto.PALOMITAS);
        producto1.setPrecio(new BigDecimal("15.00"));
        producto1.setStock(100);
        producto1.setActivo(true);
        producto1.setImagenUrl("/img/palomitas-grandes.jpg");

        // Bebidas
        Producto producto2 = new Producto();
        producto2.setId(contador.getAndIncrement());
        producto2.setNombre("Coca-Cola Grande");
        producto2.setDescripcion("Coca-Cola con hielo en vaso grande");
        producto2.setCategoria(CategoriaProducto.BEBIDAS);
        producto2.setPrecio(new BigDecimal("10.00"));
        producto2.setStock(150);
        producto2.setActivo(true);
        producto2.setImagenUrl("/img/cocacola-grande.jpg");

        // Chocolate
        Producto producto3 = new Producto();
        producto3.setId(contador.getAndIncrement());
        producto3.setNombre("Chocolate M&Ms");
        producto3.setDescripcion("Chocolate M&Ms clásicos");
        producto3.setCategoria(CategoriaProducto.CHOCOLATE);
        producto3.setPrecio(new BigDecimal("8.50"));
        producto3.setStock(80);
        producto3.setActivo(true);
        producto3.setImagenUrl("/img/mms.jpg");

        // Golosinas
        Producto producto4 = new Producto();
        producto4.setId(contador.getAndIncrement());
        producto4.setNombre("Gomitas Surtidas");
        producto4.setDescripcion("Bolsa de gomitas de frutas variadas");
        producto4.setCategoria(CategoriaProducto.GOLOSINAS);
        producto4.setPrecio(new BigDecimal("7.50"));
        producto4.setStock(60);
        producto4.setActivo(true);
        producto4.setImagenUrl("/img/gomitas.jpg");

        // Combo
        Producto producto5 = new Producto();
        producto5.setId(contador.getAndIncrement());
        producto5.setNombre("Combo Familiar");
        producto5.setDescripcion("2 palomitas grandes, 2 bebidas grandes y 1 chocolate");
        producto5.setCategoria(CategoriaProducto.COMBOS);
        producto5.setPrecio(new BigDecimal("45.00"));
        producto5.setStock(30);
        producto5.setActivo(true);
        producto5.setImagenUrl("/img/combo-familiar.jpg");

        productos.add(producto1);
        productos.add(producto2);
        productos.add(producto3);
        productos.add(producto4);
        productos.add(producto5);
    }
}