package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.model.Cliente;
import com.utp.cinerama.cinerama.model.Pago;
import com.utp.cinerama.cinerama.service.ClienteService;
import com.utp.cinerama.cinerama.service.PagoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*") // Permitir solicitudes de cualquier origen
public class PagoController {

    @Autowired
    private PagoService pagoService;
    
    @Autowired
    private ClienteService clienteService;

    // GET /api/pagos - Obtener todos los pagos
    @GetMapping
    public ResponseEntity<List<Pago>> obtenerTodosLosPagos() {
        // Inicializar datos de prueba si es necesario
        pagoService.inicializarDatosPrueba();
        
        List<Pago> pagos = pagoService.obtenerTodosLosPagos();
        return ResponseEntity.ok(pagos);
    }

    // GET /api/pagos/{id} - Obtener pago por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pago> obtenerPagoPorId(@PathVariable Long id) {
        Optional<Pago> pago = pagoService.obtenerPagoPorId(id);
        
        return pago.isPresent() 
            ? ResponseEntity.ok(pago.get()) 
            : ResponseEntity.notFound().build();
    }

    // GET /api/pagos/referencia/{referencia} - Buscar pago por referencia
    @GetMapping("/referencia/{referencia}")
    public ResponseEntity<Pago> obtenerPagoPorReferencia(@PathVariable String referencia) {
        Optional<Pago> pago = pagoService.obtenerPagoPorReferencia(referencia);
        
        return pago.isPresent() 
            ? ResponseEntity.ok(pago.get()) 
            : ResponseEntity.notFound().build();
    }

    // POST /api/pagos/boletos - Crear pago para boletos
    @PostMapping("/boletos")
    public ResponseEntity<?> crearPagoParaBoletos(
            @RequestParam List<Long> boletosIds,
            @RequestParam Long clienteId,
            @RequestParam Pago.MetodoPago metodoPago,
            @RequestParam Pago.TipoComprobante tipoComprobante) {
        
        try {
            // Verificar que el cliente existe
            Optional<Cliente> clienteOpt = clienteService.obtenerClientePorId(clienteId);
            if (clienteOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\": \"Cliente no encontrado\"}");
            }
            
            Pago nuevoPago = pagoService.crearPagoParaBoletos(boletosIds, clienteOpt.get(), 
                                                          metodoPago, tipoComprobante);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPago);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // POST /api/pagos/productos - Crear pago para venta de productos
    @PostMapping("/productos")
    public ResponseEntity<?> crearPagoParaVentaProducto(
            @RequestParam Long ventaProductoId,
            @RequestParam Long clienteId,
            @RequestParam Pago.MetodoPago metodoPago,
            @RequestParam Pago.TipoComprobante tipoComprobante) {
        
        try {
            // Verificar que el cliente existe
            Optional<Cliente> clienteOpt = clienteService.obtenerClientePorId(clienteId);
            if (clienteOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\": \"Cliente no encontrado\"}");
            }
            
            Pago nuevoPago = pagoService.crearPagoParaVentaProducto(ventaProductoId, clienteOpt.get(), 
                                                              metodoPago, tipoComprobante);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPago);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // POST /api/pagos/{id}/tarjeta - Procesar pago con tarjeta
    @PostMapping("/{id}/tarjeta")
    public ResponseEntity<?> procesarPagoTarjeta(
            @PathVariable Long id,
            @RequestParam String numeroTarjeta) {
        
        try {
            Pago pago = pagoService.procesarPagoTarjeta(id, numeroTarjeta);
            return ResponseEntity.ok(pago);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // POST /api/pagos/{id}/app - Procesar pago con app (Yape o Plin)
    @PostMapping("/{id}/app")
    public ResponseEntity<?> procesarPagoApp(@PathVariable Long id) {
        try {
            Pago pago = pagoService.procesarPagoApp(id);
            return ResponseEntity.ok(pago);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // GET /api/pagos/cliente/{clienteId} - Buscar pagos por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pago>> buscarPagosPorCliente(@PathVariable Long clienteId) {
        List<Pago> pagos = pagoService.buscarPagosPorCliente(clienteId);
        return ResponseEntity.ok(pagos);
    }

    // GET /api/pagos/fecha/{fecha} - Buscar pagos por fecha
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<Pago>> buscarPagosPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<Pago> pagos = pagoService.buscarPagosPorFecha(fecha);
        return ResponseEntity.ok(pagos);
    }

    // GET /api/pagos/estado/{estado} - Buscar pagos por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pago>> buscarPagosPorEstado(@PathVariable Pago.EstadoPago estado) {
        List<Pago> pagos = pagoService.buscarPagosPorEstado(estado);
        return ResponseEntity.ok(pagos);
    }

    // GET /api/pagos/reporte/fecha/{fecha} - Obtener total ventas por fecha
    @GetMapping("/reporte/fecha/{fecha}")
    public ResponseEntity<BigDecimal> obtenerTotalVentasPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        BigDecimal total = pagoService.calcularTotalVentasPorFecha(fecha);
        return ResponseEntity.ok(total);
    }
}