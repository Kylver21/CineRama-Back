package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.model.Producto;
import com.utp.cinerama.cinerama.model.Producto.CategoriaProducto;
import com.utp.cinerama.cinerama.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // GET /api/productos - Obtener todos los productos
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodosLosProductos() {
        List<Producto> productos = productoService.obtenerTodosLosProductos();
        return ResponseEntity.ok(productos);
    }

    // GET /api/productos/{id} - Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        Optional<Producto> producto = productoService.obtenerProductoPorId(id);
        
        if (producto.isPresent()) {
            return ResponseEntity.ok(producto.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/productos - Crear nuevo producto
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.crearProducto(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // PUT /api/productos/{id} - Actualizar producto completo
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        try {
            Optional<Producto> productoActualizado = productoService.actualizarProducto(id, producto);
            
            if (productoActualizado.isPresent()) {
                return ResponseEntity.ok(productoActualizado.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // PATCH /api/productos/{id} - Actualización parcial
    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizarProductoParcial(@PathVariable Long id, @RequestBody Producto productoParcial) {
        try {
            Optional<Producto> productoActualizado = productoService.actualizarProductoParcial(id, productoParcial);
            
            if (productoActualizado.isPresent()) {
                return ResponseEntity.ok(productoActualizado.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // DELETE /api/productos/{id} - Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        boolean eliminado = productoService.eliminarProducto(id);
        
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/productos/categoria/{categoria} - Buscar productos por categoría
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> buscarPorCategoria(@PathVariable CategoriaProducto categoria) {
        List<Producto> productos = productoService.buscarPorCategoria(categoria);
        return ResponseEntity.ok(productos);
    }

    // GET /api/productos/buscar?nombre=palomitas - Buscar productos por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) CategoriaProducto categoria,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax) {
        
        List<Producto> productos;
        
        if (nombre != null) {
            productos = productoService.buscarPorNombre(nombre);
        } else if (categoria != null) {
            productos = productoService.buscarPorCategoria(categoria);
        } else if (precioMin != null && precioMax != null) {
            productos = productoService.buscarPorRangoPrecio(precioMin, precioMax);
        } else {
            productos = productoService.obtenerTodosLosProductos();
        }
        
        return ResponseEntity.ok(productos);
    }

    // GET /api/productos/activos - Obtener solo productos activos
    @GetMapping("/activos")
    public ResponseEntity<List<Producto>> obtenerProductosActivos() {
        List<Producto> productos = productoService.buscarProductosActivos();
        return ResponseEntity.ok(productos);
    }

    // GET /api/productos/con-stock - Obtener solo productos con stock
    @GetMapping("/con-stock")
    public ResponseEntity<List<Producto>> obtenerProductosConStock() {
        List<Producto> productos = productoService.buscarProductosConStock();
        return ResponseEntity.ok(productos);
    }

    // POST /api/productos/{id}/stock - Actualizar stock
    @PostMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(
            @PathVariable Long id, 
            @RequestParam int cantidad) {
        
        boolean actualizado = productoService.actualizarStock(id, cantidad);
        
        if (actualizado) {
            return ResponseEntity.ok("{\"mensaje\": \"Stock actualizado correctamente\"}");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/productos/{id}/reducir-stock - Reducir stock (para ventas)
    @PostMapping("/{id}/reducir-stock")
    public ResponseEntity<?> reducirStock(
            @PathVariable Long id, 
            @RequestParam int cantidad) {
        
        boolean reducido = productoService.reducirStock(id, cantidad);
        
        if (reducido) {
            return ResponseEntity.ok("{\"mensaje\": \"Stock reducido correctamente\"}");
        } else {
            return ResponseEntity.badRequest()
                    .body("{\"error\": \"No hay suficiente stock disponible o el producto no existe\"}");
        }
    }

    // POST /api/productos/{id}/aumentar-stock - Aumentar stock (para reposiciones)
    @PostMapping("/{id}/aumentar-stock")
    public ResponseEntity<?> aumentarStock(
            @PathVariable Long id, 
            @RequestParam int cantidad) {
        
        boolean aumentado = productoService.aumentarStock(id, cantidad);
        
        if (aumentado) {
            return ResponseEntity.ok("{\"mensaje\": \"Stock aumentado correctamente\"}");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}