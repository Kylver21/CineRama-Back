package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.model.Sala;
import com.utp.cinerama.cinerama.model.Sala.TipoSala;
import com.utp.cinerama.cinerama.service.SalaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/salas")
@CrossOrigin(origins = "*")
public class SalaController {

    @Autowired
    private SalaService salaService;

    // GET /api/salas - Obtener todas las salas
    @GetMapping
    public ResponseEntity<List<Sala>> obtenerTodasLasSalas() {
        List<Sala> salas = salaService.obtenerTodasLasSalas();
        return ResponseEntity.ok(salas);
    }

    // GET /api/salas/{id} - Obtener sala por ID
    @GetMapping("/{id}")
    public ResponseEntity<Sala> obtenerSalaPorId(@PathVariable Long id) {
        Optional<Sala> sala = salaService.obtenerSalaPorId(id);
        
        if (sala.isPresent()) {
            return ResponseEntity.ok(sala.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/salas - Crear nueva sala
    @PostMapping
    public ResponseEntity<?> crearSala(@RequestBody Sala sala) {
        try {
            Sala nuevaSala = salaService.crearSala(sala);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSala);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // PUT /api/salas/{id} - Actualizar sala completa
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSala(@PathVariable Long id, @RequestBody Sala sala) {
        try {
            Optional<Sala> salaActualizada = salaService.actualizarSala(id, sala);
            
            if (salaActualizada.isPresent()) {
                return ResponseEntity.ok(salaActualizada.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // PATCH /api/salas/{id} - Actualización parcial
    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizarSalaParcial(@PathVariable Long id, @RequestBody Sala salaParcial) {
        try {
            Optional<Sala> salaActualizada = salaService.actualizarSalaParcial(id, salaParcial);
            
            if (salaActualizada.isPresent()) {
                return ResponseEntity.ok(salaActualizada.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // DELETE /api/salas/{id} - Eliminar sala
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSala(@PathVariable Long id) {
        boolean eliminada = salaService.eliminarSala(id);
        
        if (eliminada) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/salas/nombre/{nombre} - Buscar sala por nombre
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Sala> buscarPorNombre(@PathVariable String nombre) {
        Optional<Sala> sala = salaService.buscarPorNombre(nombre);
        
        if (sala.isPresent()) {
            return ResponseEntity.ok(sala.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/salas/tipo/{tipo} - Buscar salas por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Sala>> buscarPorTipo(@PathVariable TipoSala tipo) {
        List<Sala> salas = salaService.buscarPorTipo(tipo);
        return ResponseEntity.ok(salas);
    }

    // GET /api/salas/activas - Obtener salas activas
    @GetMapping("/activas")
    public ResponseEntity<List<Sala>> obtenerSalasActivas() {
        List<Sala> salas = salaService.buscarSalasActivas();
        return ResponseEntity.ok(salas);
    }

    // GET /api/salas/2d - Obtener salas 2D
    @GetMapping("/2d")
    public ResponseEntity<List<Sala>> obtenerSalas2D() {
        List<Sala> salas = salaService.buscarSalas2D();
        return ResponseEntity.ok(salas);
    }

    // GET /api/salas/capacidad/{capacidad} - Buscar salas por capacidad mínima
    @GetMapping("/capacidad/{capacidad}")
    public ResponseEntity<List<Sala>> buscarPorCapacidad(@PathVariable Integer capacidad) {
        List<Sala> salas = salaService.buscarPorCapacidadMinima(capacidad);
        return ResponseEntity.ok(salas);
    }

    // POST /api/salas/{id}/activar - Activar sala
    @PostMapping("/{id}/activar")
    public ResponseEntity<?> activarSala(@PathVariable Long id) {
        boolean activada = salaService.activarSala(id);
        
        if (activada) {
            return ResponseEntity.ok("{\"mensaje\": \"Sala activada exitosamente\"}");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/salas/{id}/desactivar - Desactivar sala
    @PostMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivarSala(@PathVariable Long id) {
        boolean desactivada = salaService.desactivarSala(id);
        
        if (desactivada) {
            return ResponseEntity.ok("{\"mensaje\": \"Sala desactivada exitosamente\"}");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/salas/buscar - Buscar con múltiples criterios
    @GetMapping("/buscar")
    public ResponseEntity<List<Sala>> buscarSalas(
            @RequestParam(required = false) TipoSala tipo,
            @RequestParam(required = false) Boolean activa,
            @RequestParam(required = false) Integer capacidadMinima,
            @RequestParam(required = false) String nombre) {
        
        List<Sala> salas;
        
        if (nombre != null) {
            Optional<Sala> sala = salaService.buscarPorNombre(nombre);
            salas = sala.map(List::of).orElse(List.of());
        } else if (tipo != null) {
            salas = salaService.buscarPorTipo(tipo);
        } else if (activa != null && activa) {
            salas = salaService.buscarSalasActivas();
        } else if (capacidadMinima != null) {
            salas = salaService.buscarPorCapacidadMinima(capacidadMinima);
        } else {
            salas = salaService.obtenerTodasLasSalas();
        }
        
        return ResponseEntity.ok(salas);
    }

    // GET /api/salas/validar/nombre/{nombre} - Verificar si existe un nombre
    @GetMapping("/validar/nombre/{nombre}")
    public ResponseEntity<?> validarNombre(@PathVariable String nombre) {
        boolean existe = salaService.existeNombre(nombre);
        return ResponseEntity.ok("{\"existe\": " + existe + "}");
    }
}
