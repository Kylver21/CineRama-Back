package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.model.Boleto;
import com.utp.cinerama.cinerama.model.Boleto.EstadoBoleto;
import com.utp.cinerama.cinerama.service.BoletoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/boletos")
@CrossOrigin(origins = "*")
public class BoletoController {

    @Autowired
    private BoletoService boletoService;

    // GET /api/boletos - Obtener todos los boletos
    @GetMapping
    public ResponseEntity<List<Boleto>> obtenerTodosLosBoletos() {
        List<Boleto> boletos = boletoService.obtenerTodosLosBoletos();
        return ResponseEntity.ok(boletos);
    }

    // GET /api/boletos/{id} - Obtener boleto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Boleto> obtenerBoletoPorId(@PathVariable Long id) {
        Optional<Boleto> boleto = boletoService.obtenerBoletoPorId(id);
        
        if (boleto.isPresent()) {
            return ResponseEntity.ok(boleto.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/boletos - Crear nuevo boleto (reservar/comprar)
    @PostMapping
    public ResponseEntity<?> crearBoleto(@RequestBody Boleto boleto) {
        try {
            Boleto nuevoBoleto = boletoService.crearBoleto(boleto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoBoleto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // PUT /api/boletos/{id} - Actualizar boleto completo
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarBoleto(@PathVariable Long id, @RequestBody Boleto boleto) {
        try {
            Optional<Boleto> boletoActualizado = boletoService.actualizarBoleto(id, boleto);
            
            if (boletoActualizado.isPresent()) {
                return ResponseEntity.ok(boletoActualizado.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // DELETE /api/boletos/{id} - Eliminar boleto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarBoleto(@PathVariable Long id) {
        boolean eliminado = boletoService.eliminarBoleto(id);
        
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/boletos/{id}/pagar - Pagar un boleto reservado
    @PostMapping("/{id}/pagar")
    public ResponseEntity<?> pagarBoleto(@PathVariable Long id) {
        boolean pagado = boletoService.pagarBoleto(id);
        
        if (pagado) {
            return ResponseEntity.ok("{\"mensaje\": \"Boleto pagado exitosamente\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"error\": \"No se pudo pagar el boleto. Verifique que esté en estado RESERVADO\"}");
        }
    }

    // POST /api/boletos/{id}/cancelar - Cancelar un boleto
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarBoleto(@PathVariable Long id) {
        boolean cancelado = boletoService.cancelarBoleto(id);
        
        if (cancelado) {
            return ResponseEntity.ok("{\"mensaje\": \"Boleto cancelado exitosamente\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"error\": \"No se pudo cancelar el boleto\"}");
        }
    }

    // POST /api/boletos/{id}/usar - Marcar boleto como usado
    @PostMapping("/{id}/usar")
    public ResponseEntity<?> marcarBoletoComoUsado(@PathVariable Long id) {
        boolean usado = boletoService.marcarBoletoComoUsado(id);
        
        if (usado) {
            return ResponseEntity.ok("{\"mensaje\": \"Boleto marcado como usado exitosamente\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"error\": \"No se pudo marcar el boleto como usado. Verifique que esté PAGADO\"}");
        }
    }

    // GET /api/boletos/cliente/{clienteId} - Obtener boletos de un cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Boleto>> obtenerBoletosPorCliente(@PathVariable Long clienteId) {
        List<Boleto> boletos = boletoService.buscarPorCliente(clienteId);
        return ResponseEntity.ok(boletos);
    }

    // GET /api/boletos/funcion/{funcionId} - Obtener boletos de una función
    @GetMapping("/funcion/{funcionId}")
    public ResponseEntity<List<Boleto>> obtenerBoletosPorFuncion(@PathVariable Long funcionId) {
        List<Boleto> boletos = boletoService.buscarPorFuncion(funcionId);
        return ResponseEntity.ok(boletos);
    }

    // GET /api/boletos/estado/{estado} - Obtener boletos por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Boleto>> obtenerBoletosPorEstado(@PathVariable EstadoBoleto estado) {
        List<Boleto> boletos = boletoService.buscarPorEstado(estado);
        return ResponseEntity.ok(boletos);
    }

    // GET /api/boletos/validos - Obtener boletos válidos (RESERVADO o PAGADO)
    @GetMapping("/validos")
    public ResponseEntity<List<Boleto>> obtenerBoletosValidos() {
        List<Boleto> boletos = boletoService.buscarBoletosValidos();
        return ResponseEntity.ok(boletos);
    }

    // GET /api/boletos/funcion/{funcionId}/asientos-ocupados - Obtener asientos ocupados de una función
    @GetMapping("/funcion/{funcionId}/asientos-ocupados")
    public ResponseEntity<List<String>> obtenerAsientosOcupados(@PathVariable Long funcionId) {
        List<String> asientosOcupados = boletoService.obtenerAsientosOcupados(funcionId);
        return ResponseEntity.ok(asientosOcupados);
    }

    // GET /api/boletos/funcion/{funcionId}/asiento/{asiento} - Verificar si un asiento está ocupado
    @GetMapping("/funcion/{funcionId}/asiento/{asiento}")
    public ResponseEntity<?> verificarAsiento(@PathVariable Long funcionId, @PathVariable String asiento) {
        Optional<Boleto> boleto = boletoService.buscarPorFuncionYAsiento(funcionId, asiento);
        
        if (boleto.isPresent()) {
            return ResponseEntity.ok("{\"ocupado\": true, \"boleto\": " + boleto.get().getId() + "}");
        } else {
            return ResponseEntity.ok("{\"ocupado\": false}");
        }
    }

    // GET /api/boletos/funcion/{funcionId}/estadisticas - Obtener estadísticas de una función
    @GetMapping("/funcion/{funcionId}/estadisticas")
    public ResponseEntity<?> obtenerEstadisticasFuncion(@PathVariable Long funcionId) {
        BigDecimal ingresos = boletoService.calcularIngresosPorFuncion(funcionId);
        long boletosVendidos = boletoService.contarBoletosVendidos(funcionId);
        List<String> asientosOcupados = boletoService.obtenerAsientosOcupados(funcionId);
        
        String estadisticas = "{" +
                "\"ingresos\": " + ingresos + "," +
                "\"boletosVendidos\": " + boletosVendidos + "," +
                "\"totalAsientosOcupados\": " + asientosOcupados.size() + "," +
                "\"asientosOcupados\": " + asientosOcupados +
                "}";
        
        return ResponseEntity.ok(estadisticas);
    }

    // GET /api/boletos/buscar - Buscar boletos con múltiples criterios
    @GetMapping("/buscar")
    public ResponseEntity<List<Boleto>> buscarBoletos(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long funcionId,
            @RequestParam(required = false) EstadoBoleto estado) {
        
        List<Boleto> boletos;
        
        if (clienteId != null) {
            boletos = boletoService.buscarPorCliente(clienteId);
        } else if (funcionId != null) {
            boletos = boletoService.buscarPorFuncion(funcionId);
        } else if (estado != null) {
            boletos = boletoService.buscarPorEstado(estado);
        } else {
            boletos = boletoService.obtenerTodosLosBoletos();
        }
        
        return ResponseEntity.ok(boletos);
    }
}
