package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.model.Cliente;
import com.utp.cinerama.cinerama.model.VentaProducto;
import com.utp.cinerama.cinerama.service.ClienteService;
import com.utp.cinerama.cinerama.service.VentaProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas-productos")
@CrossOrigin(origins = "*")
public class VentaProductoController {

    @Autowired
    private VentaProductoService ventaProductoService;
    
    @Autowired
    private ClienteService clienteService;

    // GET /api/ventas-productos - Obtener todas las ventas
    @GetMapping
    public ResponseEntity<List<VentaProducto>> obtenerTodasLasVentas() {
        // Inicializar datos de prueba si es necesario
        ventaProductoService.inicializarDatosPrueba();
        
        List<VentaProducto> ventas = ventaProductoService.obtenerTodasLasVentas();
        return ResponseEntity.ok(ventas);
    }

    // GET /api/ventas-productos/{id} - Obtener venta por ID
    @GetMapping("/{id}")
    public ResponseEntity<VentaProducto> obtenerVentaPorId(@PathVariable Long id) {
        Optional<VentaProducto> venta = ventaProductoService.obtenerVentaPorId(id);
        
        if (venta.isPresent()) {
            return ResponseEntity.ok(venta.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/ventas-productos - Crear nueva venta
    @PostMapping
    public ResponseEntity<?> crearVenta(
            @RequestParam(required = false) Long clienteId,
            @RequestParam String metodoPago) {
        
        try {
            Cliente cliente = null;
            if (clienteId != null) {
                Optional<Cliente> clienteOpt = clienteService.obtenerClientePorId(clienteId);
                if (clienteOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body("{\"error\": \"Cliente no encontrado\"}");
                }
                cliente = clienteOpt.get();
            }
            
            VentaProducto nuevaVenta = ventaProductoService.crearVenta(cliente, metodoPago);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // POST /api/ventas-productos/{id}/productos - Agregar producto a la venta
    @PostMapping("/{id}/productos")
    public ResponseEntity<?> agregarProducto(
            @PathVariable Long id,
            @RequestParam Long productoId,
            @RequestParam Integer cantidad) {
        
        try {
            VentaProducto venta = ventaProductoService.agregarProducto(id, productoId, cantidad);
            return ResponseEntity.ok(venta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // DELETE /api/ventas-productos/{id}/productos/{productoId} - Eliminar producto
    @DeleteMapping("/{id}/productos/{productoId}")
    public ResponseEntity<?> eliminarProducto(
            @PathVariable Long id,
            @PathVariable Long productoId) {
        
        try {
            VentaProducto venta = ventaProductoService.eliminarProducto(id, productoId);
            return ResponseEntity.ok(venta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // PUT /api/ventas-productos/{id}/productos/{productoId} - Actualizar cantidad
    @PutMapping("/{id}/productos/{productoId}")
    public ResponseEntity<?> actualizarCantidadProducto(
            @PathVariable Long id,
            @PathVariable Long productoId,
            @RequestParam Integer cantidad) {
        
        try {
            VentaProducto venta = ventaProductoService.actualizarCantidadProducto(id, productoId, cantidad);
            return ResponseEntity.ok(venta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // POST /api/ventas-productos/{id}/completar - Completar venta
    @PostMapping("/{id}/completar")
    public ResponseEntity<?> completarVenta(@PathVariable Long id) {
        try {
            VentaProducto venta = ventaProductoService.completarVenta(id);
            return ResponseEntity.ok(venta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // GET /api/ventas-productos/cliente/{clienteId} - Buscar por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VentaProducto>> buscarPorCliente(@PathVariable Long clienteId) {
        List<VentaProducto> ventas = ventaProductoService.buscarPorCliente(clienteId);
        return ResponseEntity.ok(ventas);
    }

    // GET /api/ventas-productos/fecha/{fecha} - Buscar por fecha
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<VentaProducto>> buscarPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<VentaProducto> ventas = ventaProductoService.buscarPorFecha(fecha);
        return ResponseEntity.ok(ventas);
    }

    // GET /api/ventas-productos/completadas - Obtener ventas completadas
    @GetMapping("/completadas")
    public ResponseEntity<List<VentaProducto>> obtenerVentasCompletadas() {
        List<VentaProducto> ventas = ventaProductoService.buscarVentasCompletadas();
        return ResponseEntity.ok(ventas);
    }

    // GET /api/ventas-productos/pendientes - Obtener ventas pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<List<VentaProducto>> obtenerVentasPendientes() {
        List<VentaProducto> ventas = ventaProductoService.buscarVentasPendientes();
        return ResponseEntity.ok(ventas);
    }
}